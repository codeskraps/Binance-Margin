package com.codeskraps.core.client

import android.util.Log
import com.binance.connector.client.SpotClient
import com.codeskraps.core.client.adapters.CandleAdapter
import com.codeskraps.core.client.model.AssetInfoDto
import com.codeskraps.core.client.model.CandleDto
import com.codeskraps.core.client.model.InterestDto
import com.codeskraps.core.client.model.InterestHistoryDto
import com.codeskraps.core.client.model.Interval
import com.codeskraps.core.client.model.MarginAccountDto
import com.codeskraps.core.client.model.OrderDto
import com.codeskraps.core.client.model.TickerDto
import com.codeskraps.core.client.model.TradeDto
import com.codeskraps.core.client.model.TransferDto
import com.codeskraps.core.client.model.TransferHistoryDto
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
import javax.inject.Inject


class BinanceClient @Inject constructor(
    private val client: SpotClient,
    private val store: BinanceStore
) {
    companion object {
        const val BASE_ASSET = "USDT"
        private val TAG = BinanceClient::class.java.simpleName
    }

    private val margin by lazy { client.createMargin() }
    private val market by lazy { client.createMarket() }
    private val moshi by lazy { Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build() }
    private val moshiCandles by lazy { Moshi.Builder().add(CandleAdapter()).build() }

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

    fun tickerSymbol(symbols: ArrayList<String>): List<TickerDto> {
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
        }.getOrElse { emptyList() }
    }

    fun kLines(symbol: String, interval: Interval, limit: Int): List<CandleDto> {
        return runCatching {
            try {
                val parameters: MutableMap<String, Any> = LinkedHashMap()
                parameters["symbol"] = symbol
                parameters["interval"] = interval.value
                parameters["limit"] = limit

                val result = market.klines(parameters)
                Log.i(TAG, result)

                val listType = Types.newParameterizedType(List::class.java, CandleDto::class.java)
                val adapter = moshiCandles.adapter<List<CandleDto>>(listType)


                val dataList = adapter.fromJson(result)
                Log.e(TAG, dataList.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }

            emptyList<CandleDto>()
        }.getOrElse { emptyList() }
    }

    private fun transferHistory(): TransferHistoryDto {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["startTime"] = transferStartTime(store.startDate)
            val json = margin.transferHistory(parameters)

            val jsonAdapter: JsonAdapter<TransferHistoryDto> =
                moshi.adapter(TransferHistoryDto::class.java)
            return@runCatching jsonAdapter.fromJson(json) ?: TransferHistoryDto()
        }.getOrElse { TransferHistoryDto() }
    }

    fun transfers(): List<TransferDto> = transferHistory().rows

    suspend fun trades(symbols: List<String>): List<TradeDto> {
        return coroutineScope {
            val allTrades = mutableListOf<TradeDto>()
            val deferredResults = symbols.map { symbol -> async { trades(symbol) } }

            val results = awaitAll(*deferredResults.toTypedArray())
            results.forEach { allTrades.addAll(it) }

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
        }.getOrElse { emptyList() }
    }

    suspend fun orders(symbols: List<String>): List<OrderDto> {
        return coroutineScope {
            //val allSymbols = allSymbols()
            val allTrades = mutableListOf<OrderDto>()
            val deferredResults = symbols.map { symbol -> async { orders(symbol) } }
            val result = awaitAll(*deferredResults.toTypedArray())
            result.forEach { allTrades.addAll(it) }
            return@coroutineScope allTrades.sortedBy { it.time }.reversed()
        }
    }

    private fun orders(symbol: String): List<OrderDto> {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["symbol"] = symbol

            val json = margin.getAllOrders(parameters)
            val listMyData = Types.newParameterizedType(
                MutableList::class.java,
                OrderDto::class.java
            )
            val jsonAdapter: JsonAdapter<List<OrderDto>> = moshi.adapter(listMyData)
            return@runCatching jsonAdapter.fromJson(json)
                ?.filter { it.time >= store.startDate }
                ?.filter { it.status != "FILLED" }
                ?.filter { it.status != "CANCELED" }
                ?.sortedBy { it.time }?.reversed()
                ?: emptyList()
        }.getOrElse { emptyList() }
    }

    private fun allSymbols(): List<String> {
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
        }.getOrElse { emptyList() }
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
        }.getOrElse { emptyList() }
    }

    private fun sanitizeSymbols(symbols: ArrayList<String>): ArrayList<String> {
        val newSymbols = ArrayList(symbols)
        newSymbols.removeAll(badSymbols())
        newSymbols.remove("BTC$BASE_ASSET")
        newSymbols.add("BTC$BASE_ASSET")
        return newSymbols
    }

    private fun badSymbols(): Set<String> {
        return setOf("$BASE_ASSET$BASE_ASSET", "FDUSD$BASE_ASSET", "USDTUSDT")
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
}