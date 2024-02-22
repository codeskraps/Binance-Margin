package com.codeskraps.core.client

import android.util.Log
import com.binance.connector.client.SpotClient
import com.codeskraps.core.client.adapters.CandleAdapter
import com.codeskraps.core.client.model.Candle
import com.codeskraps.core.client.model.Interval
import com.codeskraps.core.client.model.MarginAccountDto
import com.codeskraps.core.client.model.Order
import com.codeskraps.core.client.model.TickerDto
import com.codeskraps.core.client.model.Trade
import com.codeskraps.core.client.model.TransferHistory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject


class BinanceClient @Inject constructor(
    private val client: SpotClient,
    private val store: BinanceStore
) {
    companion object {
        const val BASE_ASSET = "USDT"
        private const val START_TIME = 1707414465000
        private val TAG = BinanceClient::class.java.simpleName
    }

    private val margin by lazy { client.createMargin() }
    private val market by lazy { client.createMarket() }
    private val moshi by lazy { Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build() }
    private val moshiCandles by lazy { Moshi.Builder().add(CandleAdapter()).build() }

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
                val sorted = transferHistory.rows.filter { it.timestamp >= START_TIME }
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

    fun trades(symbols: List<String>): List<Trade> {
        val allTrades = mutableListOf<Trade>()
        symbols.forEach { symbol ->
            allTrades.addAll(trades(symbol))
        }
        return allTrades.sortedBy { it.time }.reversed()
    }

    fun trades(symbol: String): List<Trade> {
        return runCatching {
            if (!badSymbols().contains(symbol)) {
                val parameters: MutableMap<String, Any> = LinkedHashMap()
                parameters["symbol"] = symbol

                val json = margin.trades(parameters)
                val listMyData = Types.newParameterizedType(
                    MutableList::class.java,
                    Trade::class.java
                )
                val jsonAdapter: JsonAdapter<List<Trade>> = moshi.adapter(listMyData)
                return@runCatching jsonAdapter.fromJson(json)
                    ?.filter { it.time >= START_TIME }
                    ?.sortedBy { it.time }?.reversed()
                    ?: emptyList()
            }
            return@runCatching emptyList<Trade>()
        }.getOrElse { emptyList() }
    }

    fun orders(symbols: List<String>): List<Order> {
        val allTrades = mutableListOf<Order>()
        symbols.forEach { symbol ->
            allTrades.addAll(orders(symbol))
        }
        return allTrades.sortedBy { it.time }.reversed()
    }

    fun orders(symbol: String): List<Order> {
        return runCatching {
            val parameters: MutableMap<String, Any> = LinkedHashMap()
            parameters["symbol"] = symbol

            val json = margin.getAllOrders(parameters)
            val listMyData = Types.newParameterizedType(
                MutableList::class.java,
                Order::class.java
            )
            val jsonAdapter: JsonAdapter<List<Order>> = moshi.adapter(listMyData)
            return@runCatching jsonAdapter.fromJson(json)
                ?.filter { it.time >= START_TIME }
                ?.filter { it.status != "FILLED" }
                ?.filter { it.status != "CANCELED" }
                ?.sortedBy { it.time }?.reversed()
                ?: emptyList()
        }.getOrElse { emptyList() }
    }

    fun entryPrice(symbol: String): Double {
        return runCatching {
            val trades = trades(symbol).reversed()
            var holdingPrice = .0
            var holdingQty = .0

            if (trades.isEmpty()) {
                if (symbol == "BONKUSDT") return@runCatching 0.00001063
                else return@runCatching .0
            } else if (symbol == "BONKUSDT") {
                holdingPrice = 0.00001063
                holdingQty = 4703668.0
            }


            trades.forEach { trade ->
                if (holdingQty == .0) {
                    holdingQty = trade.qty
                    holdingPrice = trade.price
                } else if (trade.isBuyer) {
                    // opening longs
                    holdingQty += trade.qty
                    holdingPrice = calculateAverageEntryPrice(
                        holdingQuantity = holdingQty,
                        holdingAveragePrice = holdingPrice,
                        tradeQuantity = trade.qty,
                        tradePrice = trade.price
                    )

                } else {
                    // closing longs
                    holdingQty -= trade.qty
                }
            }
            return@runCatching holdingPrice
        }.getOrElse { .0 }
    }

    private fun sanitizeSymbols(symbols: ArrayList<String>): ArrayList<String> {
        symbols.removeAll(badSymbols())
        symbols.remove("BTC$BASE_ASSET")
        symbols.add("BTC$BASE_ASSET")
        return symbols
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