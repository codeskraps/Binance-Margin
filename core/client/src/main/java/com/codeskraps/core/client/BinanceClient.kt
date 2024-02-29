package com.codeskraps.core.client

import android.util.Log
import com.binance.connector.client.SpotClient
import com.codeskraps.core.client.adapters.CandleAdapter
import com.codeskraps.core.client.model.Candle
import com.codeskraps.core.client.model.Interval
import com.codeskraps.core.client.model.MarginAccountDto
import com.codeskraps.core.client.model.OrderDto
import com.codeskraps.core.client.model.TickerDto
import com.codeskraps.core.client.model.TradeDto
import com.codeskraps.core.client.model.TransferHistory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject
import kotlin.math.absoluteValue


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

    fun kLines(symbol: String, interval: Interval, limit: Int): List<Candle> {
        return runCatching {
            try {
                val parameters: MutableMap<String, Any> = LinkedHashMap()
                parameters["symbol"] = symbol
                parameters["interval"] = interval.value
                parameters["limit"] = limit

                val result = market.klines(parameters)
                Log.i(TAG, result)

                val listType = Types.newParameterizedType(List::class.java, Candle::class.java)
                val adapter = moshiCandles.adapter<List<Candle>>(listType)


                val dataList = adapter.fromJson(result)
                Log.e(TAG, dataList.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }

            emptyList<Candle>()
        }.getOrElse { emptyList() }
    }

    private fun transferHistory(): TransferHistory {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            val json = margin.transferHistory(parameters)

            val jsonAdapter: JsonAdapter<TransferHistory> =
                moshi.adapter(TransferHistory::class.java)
            return@runCatching jsonAdapter.fromJson(json) ?: TransferHistory()
        }.getOrElse { TransferHistory() }
    }

    fun invested(): Double {
        return runCatching {
            val transferHistory = transferHistory()
            //Log.i(TAG, transferHistory.toString())
            val lastTransfer = transferHistory.rows.maxOfOrNull { it.timestamp }
                ?: return store.invested.toDouble()

            return@runCatching if (store.lastTransferParsed == 0L
                || store.lastTransferParsed != lastTransfer
            ) {
                val symbols = ArrayList<String>()

                transferHistory.rows.forEach { transfer ->
                    symbols.add("${transfer.asset}$BASE_ASSET")
                }

                val ticker = tickerSymbol(symbols)

                var index = 0
                val sorted = transferHistory.rows.filter { it.timestamp >= store.startDate }
                    .sortedBy { it.timestamp }.reversed()
                var invested = .0

                while (index < sorted.size && sorted[index].timestamp != store.lastTransferParsed) {
                    val transfer = sorted[index]
                    when (transfer.type) {
                        "ROLL_OUT" -> {
                            if (store.lastTransferParsed != 0L) {
                                invested -= if (transfer.asset == BASE_ASSET) {
                                    transfer.amount
                                } else {
                                    transfer.amount * ticker.first { it.symbol == "${transfer.asset}$BASE_ASSET" }.price
                                }
                            }
                        }

                        "ROLL_IN" -> {
                            invested += when (transfer.asset) {
                                "BONK" -> {
                                    50.0
                                }

                                BASE_ASSET -> {
                                    transfer.amount
                                }

                                else -> {
                                    val t =
                                        ticker.first { it.symbol == "${transfer.asset}$BASE_ASSET" }
                                    transfer.amount * t.price
                                }
                            }
                        }
                    }
                    index++
                }

                store.invested += invested.toFloat()
                store.lastTransferParsed = lastTransfer
                invested
            } else {
                store.invested.toDouble()
            }
        }.getOrElse { store.invested.toDouble() }
    }

    fun trades(symbols: List<String>): List<TradeDto> {
        val allTrades = mutableListOf<TradeDto>()
        symbols.forEach { symbol ->
            allTrades.addAll(trades(symbol))
        }
        return allTrades.sortedBy { it.time }.reversed()
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
                            price = (trade.price + it.price) / 2
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

    fun orders(symbols: List<String>): List<OrderDto> {
        val allTrades = mutableListOf<OrderDto>()
        symbols.forEach { symbol ->
            allTrades.addAll(orders(symbol))
        }
        return allTrades.sortedBy { it.time }.reversed()
    }

    fun orders(symbol: String): List<OrderDto> {
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

    fun entryPrice(symbol: String): Double {
        return runCatching {
            val trades = trades(symbol).reversed()
            var avgPrice = .0
            var totalQty = .0

            if (trades.isEmpty()) {
                if (symbol == "BONKUSDT") return@runCatching 0.00001063
                else return@runCatching .0
            } else if (symbol == "BONKUSDT") {
                avgPrice = 0.00001063
                totalQty = 4703668.0
            }

            trades.forEach { trade ->

                if (trade.isBuyer && totalQty + trade.qty > .0) {
                    // opening longs
                    if (totalQty == .0) {
                        totalQty = trade.qty
                        avgPrice = trade.price
                    } else {
                        avgPrice = calculateAverageEntryPrice(
                            holdingQuantity = totalQty,
                            holdingAveragePrice = avgPrice,
                            tradeQuantity = trade.qty,
                            tradePrice = trade.price
                        )
                        totalQty += trade.qty
                    }
                }

                if (!trade.isBuyer && totalQty - trade.qty > .0) {
                    // closing longs
                    totalQty -= trade.qty
                }

                if (!trade.isBuyer && totalQty - trade.qty < .0) {
                    // opening shorts
                    if (totalQty == .0) {
                        totalQty = -trade.qty
                        avgPrice = trade.price
                    } else {
                        avgPrice = calculateAverageEntryPrice(
                            holdingQuantity = totalQty.absoluteValue,
                            holdingAveragePrice = avgPrice,
                            tradeQuantity = trade.qty,
                            tradePrice = trade.price
                        )
                        totalQty -= trade.qty
                    }
                }

                if (trade.isBuyer && totalQty + trade.qty < .0) {
                    // closing shorts
                    totalQty += trade.qty
                }
            }
            return@runCatching avgPrice
        }.getOrElse { .0 }
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
}