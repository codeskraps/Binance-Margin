package com.codeskraps.core.client

import android.util.Log
import com.binance.connector.client.SpotClient
import com.codeskraps.core.client.adapters.CandleAdapter
import com.codeskraps.core.client.model.AssetInfoDto
import com.codeskraps.core.client.model.CandleDto
import com.codeskraps.core.client.model.InterestDto
import com.codeskraps.core.client.model.InterestHistoryDto
import com.codeskraps.core.client.model.MarginAccountDto
import com.codeskraps.core.client.model.MaxBorrowDto
import com.codeskraps.core.client.model.OrderDto
import com.codeskraps.core.client.model.TickerDto
import com.codeskraps.core.client.model.TradeDto
import com.codeskraps.core.client.model.TransferDto
import com.codeskraps.core.client.model.TransferHistoryDto
import com.neutrine.krate.rateLimiter
import com.neutrine.krate.storage.memory.memoryStateStorageWithEviction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours


class BinanceClient @Inject constructor(
    private val client: SpotClient,
    private val store: BinanceStore
) {
    companion object {
        const val BASE_ASSET = "USDT"
        private val TAG = BinanceClient::class.java.simpleName
        private const val STEP = 5
    }

    private val margin by lazy { client.createMargin() }
    private val market by lazy { client.createMarket() }
    private val moshi by lazy { Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build() }
    private val moshiCandles by lazy { Moshi.Builder().add(CandleAdapter()).build() }

    private val rateLimiter = rateLimiter(maxRate = 5) {
        maxRateTimeUnit = ChronoUnit.SECONDS
        stateStorage = memoryStateStorageWithEviction {
            ttlAfterLastAccess = 2.hours
        }
    }

    fun tradedSymbols(): List<String> = store.tradedAssets.toList().map { "$it${BASE_ASSET}" }

    fun marginAccount(): MarginAccountDto? {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["type"] = "MARGIN"

            val json = margin.account(parameters)

            val jsonAdapter: JsonAdapter<MarginAccountDto> =
                moshi.adapter(MarginAccountDto::class.java)
            jsonAdapter.fromJson(json)?.let { acc ->
                val storeAssets = store.tradedAssets

                val userAssets = acc.userAssets.filter {
                    it.netAsset != .0 || storeAssets.contains(it.asset)
                }
                val setUserAssets = userAssets.map { it.asset }.toSet()

                if (!storeAssets.containsAll(setUserAssets)) {
                    val allAssets = storeAssets.toMutableSet()
                    allAssets.addAll(userAssets.map { it.asset }.toSet())
                    store.tradedAssets = allAssets
                }

                return@runCatching acc.copy(userAssets = userAssets)
            }

            return@runCatching null
        }.getOrNull()
    }

    fun tickerSymbol(symbols: List<String>): List<TickerDto> {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["symbols"] = sanitizeSymbols(symbols)
            val json = market.tickerSymbol(parameters)

            val listMyData = Types.newParameterizedType(
                MutableList::class.java,
                TickerDto::class.java
            )
            val jsonAdapter: JsonAdapter<List<TickerDto>> = moshi.adapter(listMyData)
            return@runCatching jsonAdapter.fromJson(json) ?: emptyList()
        }.getOrElse {
            Log.e(TAG, "Ticker Symbol: $it")
            emptyList()
        }
    }

    fun kLines(symbol: String, interval: String, limit: Int): List<CandleDto> {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["symbol"] = symbol
            parameters["interval"] = interval
            parameters["limit"] = limit

            val result = market.klines(parameters)
            Log.i(TAG, result)

            val listType = Types.newParameterizedType(List::class.java, CandleDto::class.java)
            val adapter = moshiCandles.adapter<List<CandleDto>>(listType)


            val dataList = adapter.fromJson(result)
            Log.e(TAG, dataList.toString())
            dataList ?: emptyList()

        }.getOrElse {
            Log.e(TAG, "$symbol, Klines: $it")
            if (it.message?.contains("-1121") == true) {
                store.badSymbols = store.badSymbols.plus(symbol)
            }
            emptyList()
        }
    }

    private fun transferHistory(): TransferHistoryDto {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["startTime"] = transferStartTime(store.startDate)

            val json = margin.transferHistory(parameters)

            val jsonAdapter: JsonAdapter<TransferHistoryDto> =
                moshi.adapter(TransferHistoryDto::class.java)
            return@runCatching jsonAdapter.fromJson(json) ?: TransferHistoryDto()
        }.getOrElse {
            Log.e(TAG, "Transfer History: $it")
            TransferHistoryDto()
        }
    }

    fun transfers(): List<TransferDto> = transferHistory().rows

    suspend fun trades(): List<TradeDto> {
        val allSymbols = allSymbols()
        return trades(allSymbols)
    }

    suspend fun trades(symbols: List<String>): List<TradeDto> {
        return coroutineScope {
            val allTrades = mutableListOf<TradeDto>()

            for (i in symbols.indices step STEP) {
                val subSymbol = symbols.subList(i, (i + STEP).coerceAtMost(symbols.size))
                val deferredResults = subSymbol.map { symbol -> async { trades(symbol) } }
                val result = awaitAll(*deferredResults.toTypedArray())
                result.forEach {
                    allTrades.addAll(it)
                }
            }

            return@coroutineScope allTrades.sortedBy { it.time }.reversed()
        }
    }

    private fun trades(symbol: String): List<TradeDto> {
        return runCatching {
            if (!badSymbols().contains(symbol)) {
                val parameters: MutableMap<String, Any> = LinkedHashMap()
                parameters["symbol"] = symbol

                val json = margin.trades(parameters)
                val listMyData = Types.newParameterizedType(
                    MutableList::class.java,
                    TradeDto::class.java
                )
                val jsonAdapter: JsonAdapter<List<TradeDto>> = moshi.adapter(listMyData)
                val trades = jsonAdapter.fromJson(json)
                    ?.filter { it.time >= store.startDate }
                    ?.sortedBy { it.time }?.reversed()?.toMutableList()
                    ?: emptyList<TradeDto>().toMutableList()

                val mergedTrades = mutableListOf<TradeDto>()

                while (trades.isNotEmpty()) {
                    var trade = trades.removeFirst()
                    val toMerge = trades.filter { it.orderId == trade.orderId }
                    toMerge.forEach {
                        trade = trade.copy(
                            qty = trade.qty + it.qty,
                            price = calculateAverageEntryPrice(
                                trade.qty,
                                trade.price,
                                it.qty,
                                it.price
                            )
                        )
                    }
                    trades.removeAll(toMerge)
                    mergedTrades.add(trade)
                }

                return@runCatching mergedTrades.toList()
            }
            return@runCatching emptyList<TradeDto>()
        }.getOrElse {
            Log.e(TAG, "Trades: $it")
            emptyList()
        }
    }

    suspend fun orders(): List<OrderDto> {
        return coroutineScope {
            val allSymbols = allSymbols()
            val allOrders = mutableListOf<OrderDto>()

            for (i in allSymbols.indices step STEP) {
                val subSymbol = allSymbols.subList(i, (i + STEP).coerceAtMost(allSymbols.size))
                val deferredResults = subSymbol.map { symbol -> async { orders(symbol) } }
                val result = awaitAll(*deferredResults.toTypedArray())
                result.forEach { response ->
                    when (response) {
                        is Response.Success -> allOrders.addAll(response.data)
                        is Response.Failure -> Log.e(TAG, "Orders: ${response.error}")
                    }
                }
            }

            return@coroutineScope allOrders.sortedBy { it.time }.reversed()
        }
    }

    suspend fun orders(symbol: String): Response<List<OrderDto>, OrderError> {
        if (store.badSymbols.contains(symbol)) {
            return Response.Failure(OrderError.BAD_SYMBOL)
        }

        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["symbol"] = symbol

            rateLimiter.awaitUntilTake()
            val json = margin.getAllOrders(parameters)

            val listMyData = Types.newParameterizedType(
                MutableList::class.java,
                OrderDto::class.java
            )
            val jsonAdapter: JsonAdapter<List<OrderDto>> = moshi.adapter(listMyData)

            return@runCatching Response.Success(jsonAdapter.fromJson(json)
                ?.filter { it.time >= store.startDate }
                ?.filter { it.status != "FILLED" }
                ?.filter { it.status != "CANCELED" }
                ?.sortedBy { it.time }?.reversed()
                ?: emptyList()
            )
        }.getOrElse {
            Log.e(TAG, "Orders($symbol): $it")
            if (it.message?.contains("-1003") == true) {
                Response.Failure(OrderError.LIMIT_REACHED)
            } else if (it.message?.contains("-1121") == true) {
                store.badSymbols = store.badSymbols.plus(symbol)
                Response.Failure(OrderError.BAD_SYMBOL)
            } else {
                Response.Failure(OrderError.UNKNOWN)
            }
        }
    }

    fun allSymbols(): List<String> {
        return runCatching {
            val json = margin.allAssets()
            val listMyData = Types.newParameterizedType(
                MutableList::class.java,
                AssetInfoDto::class.java
            )
            val jsonAdapter: JsonAdapter<List<AssetInfoDto>> = moshi.adapter(listMyData)
            return@runCatching jsonAdapter.fromJson(json)
                ?.map { "${it.assetName}$BASE_ASSET" }
                ?: emptyList()
        }.getOrElse {
            Log.e(TAG, "All Symbols: $it")
            emptyList()
        }
    }

    private fun interestHistory(): List<InterestDto> {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["max"] = 20
            parameters["current"] = 3
            parameters["startTime"] = transferStartTime(store.startDate)
            val json = margin.interestHistory(parameters)
            val jsonAdapter: JsonAdapter<InterestHistoryDto> =
                moshi.adapter(InterestHistoryDto::class.java)
            val interestHistoryDto = jsonAdapter.fromJson(json)
            interestHistoryDto?.let {
                Log.i(TAG, "Interest History Rows: ${it.rows.size}, total: ${it.total}")
            }
            return@runCatching interestHistoryDto?.rows ?: emptyList()
        }.getOrElse {
            Log.e(TAG, "Interest History: $it")
            emptyList()
        }
    }

    fun maxBorrow(): Double {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["asset"] = BASE_ASSET

            val json = margin.maxBorrow(parameters)
            val jsonAdapter: JsonAdapter<MaxBorrowDto> =
                moshi.adapter(MaxBorrowDto::class.java)
            val maxBorrowDto = jsonAdapter.fromJson(json) ?: MaxBorrowDto()
            maxBorrowDto.amount.toDouble()
        }.getOrElse {
            Log.e(TAG, "Max Borrow: $it")
            MaxBorrowDto().amount.toDouble()
        }
    }

    private fun sanitizeSymbols(symbols: List<String>): ArrayList<String> {
        val newSymbols = ArrayList(symbols)
        newSymbols.removeAll(badSymbols())
        if (!newSymbols.contains("BTC$BASE_ASSET")) newSymbols.add("BTC$BASE_ASSET")
        return newSymbols
    }

    private fun badSymbols(): Set<String> {
        return setOf("$BASE_ASSET$BASE_ASSET", "FDUSD$BASE_ASSET", "USDCUSDT")
    }

    private fun calculateAverageEntryPrice(
        tradeQuantity: Double,
        tradePrice: Double,
        holdingQuantity: Double,
        holdingAveragePrice: Double
    ): Double {
        val totalQuantity = tradeQuantity + holdingQuantity
        val totalPrice = (tradeQuantity * tradePrice) + (holdingQuantity * holdingAveragePrice)
        return totalPrice / totalQuantity
    }

    private fun transferStartTime(timestamp: Long): Long {
        val sixMonthsAgo = LocalDate.now()
            .minusMonths(6)
            .plusDays(1)

        val date = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return timestamp.takeIf { date.isAfter(sixMonthsAgo) } ?: sixMonthsAgo.toEpochDay()
    }

    enum class OrderError : Error {
        LIMIT_REACHED,
        BAD_SYMBOL,
        UNKNOWN
    }
}