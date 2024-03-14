package com.codeskraps.core.domain.usecases.account

import android.util.Log
import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.BuildConfig
import com.codeskraps.core.domain.model.Entry
import com.codeskraps.core.domain.model.FinishTrade
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.model.TradeType
import com.codeskraps.core.domain.model.Transfer
import com.codeskraps.core.domain.usecases.trade.GetTradesUseCase
import com.codeskraps.core.domain.usecases.trade.GetTransfersUseCase
import com.codeskraps.core.realm.model.FinishedTradeEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.absoluteValue

class GetEntryPriceUseCase @Inject constructor(
    private val tradesUseCase: GetTradesUseCase,
    private val transfersUseCase: GetTransfersUseCase,
    private val putFinishTradeUseCase: PutFinishTradeUseCase
) {

    companion object {
        private val TAG = GetEntryPriceUseCase::class.java.simpleName
        private const val LOG_ASSET = "CAKE"
    }

    data class CalcData(
        var avgPrice: Double = .0,
        var totalQty: Double = .0,
        var entryTime: Long = 0,
        var exitTime: Long = 0,
        var pnl: Double = .0,
        var pnlPercent: Double = .0,
        var type: TradeType = TradeType.LONG,
        var entryPrice: Double = .0,
        var exitPrice: Double = .0,
        var trades: Int = 0
    ) {
        override fun toString(): String {
            return "avgPrice: $${avgPrice.format(8)}," +
                    " totalQty: ${totalQty.format(1)}," +
                    " value: $${(avgPrice * totalQty).format(2)}," +
                    " pnl: $${pnl.format(2)}," +
                    " pnLPercent: ${pnlPercent.format(2)}%"
        }

        private fun Double.format(digits: Int) = "%.${digits}f".format(this)
    }

    suspend operator fun invoke(symbol: String): Double {
        return coroutineScope {
            val deferredResults = listOf(
                async { tradesUseCase(listOf(symbol)) {}.first() },
                async { transfersUseCase {}.first() }
            )

            val results = awaitAll(*deferredResults.toTypedArray())

            val trades: List<Entry> = (results[0] as List<*>)
                .filterIsInstance<Trade>()
                .filter { it.symbol == symbol }
            val transfers: List<Entry> = (results[1] as List<*>)
                .filterIsInstance<Transfer>()
                .filter { "${it.asset}${BinanceClient.BASE_ASSET}" == symbol }

            val entries = listOf(trades, transfers).flatten().sortedBy { it.time() }
            var calcData = CalcData()
            log(symbol, "Start -> $symbol")

            entries.forEach { entry ->
                when (entry) {
                    is Trade -> {
                        log(symbol, "**** Trade ****")
                        calcData = calculateTrade(entry, calcData)

                        log(
                            symbol,
                            "time: ${entry.time()}," +
                                    " entry: $entry," +
                                    " value: ${(entry.price * entry.qty).format(2)}"
                        )
                        log(symbol, calcData.toString())
                    }

                    is Transfer -> {
                        log(symbol, "**** Transfer ****")
                        calcData = calculateTransfer(entry, calcData)

                        log(
                            symbol,
                            "time: ${entry.time()}," +
                                    " entry: $entry," +
                                    " value: ${(entry.price * entry.amount).format(2)}"
                        )
                        log(symbol, calcData.toString())
                    }
                }

                if (calcData.totalQty == .0 && calcData.pnl != .0) {
                    putFinishTradeUseCase(
                        FinishTrade(
                            entryTime = calcData.entryTime,
                            exitTime = calcData.exitTime,
                            symbol = symbol,
                            pnl = calcData.pnl,
                            pnlPercent = calcData.pnlPercent,
                            type = calcData.type,
                            entryPrice = calcData.entryPrice,
                            exitPrice = calcData.exitPrice,
                            trades = calcData.trades
                        )
                    )
                    calcData = CalcData()
                }
            }

            log(symbol, "Final -> $calcData")

            return@coroutineScope calcData.avgPrice
        }
    }

    private fun calculateTransfer(
        transfer: Transfer,
        calcData: CalcData
    ): CalcData {

        when (transfer.type) {
            "ROLL_OUT" -> {
                calcData.totalQty -= transfer.amount
            }

            "ROLL_IN" -> {
                calcData.entryTime = transfer.time()
                if (calcData.totalQty == 0.0) {
                    calcData.avgPrice = transfer.price
                    calcData.totalQty = transfer.amount
                } else {
                    calcData.avgPrice = calculateAverageEntryPrice(
                        transfer.amount,
                        transfer.price,
                        calcData.totalQty,
                        calcData.avgPrice
                    )
                    calcData.totalQty += transfer.amount
                }
            }
        }
        calcData.trades++

        return calcData
    }

    private fun calculateTrade(
        trade: Trade,
        calcData: CalcData
    ): CalcData {

        if (trade.isBuyer
            && (calcData.totalQty + trade.qty == .0
                    || (((calcData.totalQty + trade.qty) * calcData.avgPrice < 1.0) && ((calcData.totalQty + trade.qty) * calcData.avgPrice > -1.0) && calcData.totalQty != .0))
        ) {
            // closing shorts
            val tradePercent = ((trade.price - calcData.avgPrice) / calcData.avgPrice) * -100
            calcData.pnl += ((trade.price - calcData.avgPrice) * trade.qty) * -1
            calcData.pnlPercent = (calcData.pnlPercent + tradePercent) / 2
            calcData.entryPrice = calcData.avgPrice
            calcData.exitPrice = trade.price
            calcData.totalQty = .0
            calcData.avgPrice = .0
            calcData.exitTime = trade.time()
            calcData.type = TradeType.SHORT

            log(trade.symbol, "Finish Short -> $calcData")

        } else if (trade.isBuyer && calcData.totalQty + trade.qty > .0) {
            // opening longs
            if (calcData.totalQty == .0) {
                calcData.totalQty = trade.qty
                calcData.avgPrice = trade.price
                calcData.entryTime = trade.time()
            } else {
                calcData.avgPrice = calculateAverageEntryPrice(
                    holdingQuantity = calcData.totalQty,
                    holdingAveragePrice = calcData.avgPrice,
                    tradeQuantity = trade.qty,
                    tradePrice = trade.price
                )
                calcData.totalQty += trade.qty
            }
            calcData.trades++

            log(trade.symbol, "Opening Long -> $calcData")

        } else if (!trade.isBuyer
            && (calcData.totalQty - trade.qty == .0
                    || (((calcData.totalQty - trade.qty) * calcData.avgPrice < 1.0) && ((calcData.totalQty - trade.qty) * calcData.avgPrice > -1.0) && calcData.totalQty != .0))
        ) {
            // closing longs
            val tradePercent = ((trade.price - calcData.avgPrice) / calcData.avgPrice) * 100
            calcData.pnl += (trade.price - calcData.avgPrice) * trade.qty
            calcData.pnlPercent = (calcData.pnlPercent + tradePercent) / 2
            calcData.entryPrice = calcData.avgPrice
            calcData.exitPrice = trade.price
            calcData.totalQty = .0
            calcData.avgPrice = .0
            calcData.exitTime = trade.time()
            calcData.type = TradeType.LONG

            log(trade.symbol, "Finish Long -> $calcData")

        } else if (!trade.isBuyer && calcData.totalQty - trade.qty > .0) {
            // closing longs
            val tradePercent = ((trade.price - calcData.avgPrice) / calcData.avgPrice) * 100
            calcData.pnl += (trade.price - calcData.avgPrice) * trade.qty
            calcData.pnlPercent = (calcData.pnlPercent + tradePercent) / 2
            calcData.totalQty -= trade.qty
            calcData.trades++

            log(trade.symbol, "Taking Profit Long -> $calcData")

        } else if (!trade.isBuyer && calcData.totalQty - trade.qty < .0) {
            // opening shorts
            if (calcData.totalQty == .0) {
                calcData.totalQty = -trade.qty
                calcData.avgPrice = trade.price
                calcData.entryTime = trade.time()
            } else {
                calcData.avgPrice = calculateAverageEntryPrice(
                    holdingQuantity = calcData.totalQty.absoluteValue,
                    holdingAveragePrice = calcData.avgPrice,
                    tradeQuantity = trade.qty,
                    tradePrice = trade.price
                )
                calcData.totalQty -= trade.qty
            }
            calcData.trades++

            log(trade.symbol, "Opening Short -> $calcData")

        } else if (trade.isBuyer && calcData.totalQty + trade.qty < .0) {
            // closing shorts
            val tradePercent = ((trade.price - calcData.avgPrice) / calcData.avgPrice) * -100
            calcData.pnl += ((trade.price - calcData.avgPrice) * trade.qty) * -1
            calcData.pnlPercent = (calcData.pnlPercent + tradePercent) / 2
            calcData.totalQty += trade.qty
            calcData.trades++

            log(trade.symbol, "Taking Profit Short -> $calcData")
        }

        return calcData
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

    private fun log(symbolAsset: String, message: String) {
        if (BuildConfig.DEBUG
            && Log.isLoggable(TAG, Log.INFO)
            && symbolAsset.contains(LOG_ASSET)
        ) {
            Log.i(TAG, message)
        }
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}