package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.model.Entry
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.model.Transfer
import com.codeskraps.core.domain.usecases.trade.GetTradesUseCase
import com.codeskraps.core.domain.usecases.trade.GetTransfersUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.absoluteValue

class GetEntryPriceUseCase @Inject constructor(
    private val tradesUseCase: GetTradesUseCase,
    private val transfersUseCase: GetTransfersUseCase
) {
    suspend operator fun invoke(symbol: String): Double {
        return coroutineScope {
            val deferredResults = listOf(
                async { tradesUseCase(listOf(symbol)) {}.first() },
                async { transfersUseCase { }.first() }
            )

            val results = awaitAll(*deferredResults.toTypedArray())

            val trades: List<Entry> = (results[0] as List<*>)
                .filterIsInstance<Trade>()
                .filter { it.symbol == symbol }
            val transfers: List<Entry> = (results[1] as List<*>)
                .filterIsInstance<Transfer>()
                .filter { "${it.asset}${BinanceClient.BASE_ASSET}" == symbol }

            val entries = listOf(trades, transfers).flatten().sortedBy { it.time() }

            var avgPrice = .0
            var totalQty = .0

            entries.forEach { entry ->
                when (entry) {
                    is Trade -> {
                        /*if (entry.symbol.contains("BONK")) {
                            Log.e("GetEntryPriceUseCase", "****")
                        }*/
                        val result = calculateTrade(entry, Pair(avgPrice, totalQty))
                        avgPrice = result.first
                        totalQty = result.second

                        /*if (entry.symbol.contains("BONK")) {
                            Log.e(
                                "GetEntryPriceUseCase",
                                "time: ${entry.time()}, entry: $entry, value: ${
                                    (entry.price * entry.qty).format(
                                        2
                                    )
                                }"
                            )
                            Log.e(
                                "GetEntryPriceUseCase",
                                "avgPrice: ${avgPrice.format(8)}, totalQty: ${totalQty.format(1)}, value: ${
                                    (avgPrice * totalQty).format(
                                        2
                                    )
                                }"
                            )
                        }*/
                    }

                    is Transfer -> {
                        /*if (entry.asset.contains("BONK")) {
                            Log.e("GetEntryPriceUseCase", "****")
                        }*/
                        val result = calculateTransfer(entry, Pair(avgPrice, totalQty))
                        avgPrice = result.first
                        totalQty = result.second

                        /*if (entry.asset.contains("BONK")) {
                            Log.e(
                                "GetEntryPriceUseCase",
                                "time: ${entry.time()}, entry: $entry, value: ${
                                    (entry.price * entry.amount).format(
                                        2
                                    )
                                }"
                            )
                            Log.e(
                                "GetEntryPriceUseCase",
                                "avgPrice: ${avgPrice.format(8)}, totalQty: ${totalQty.format(1)}, value: ${
                                    (avgPrice * totalQty).format(
                                        2
                                    )
                                }"
                            )
                        }*/
                    }
                }
            }

            return@coroutineScope avgPrice
        }
    }

    private fun calculateTransfer(
        transfer: Transfer,
        entry: Pair<Double, Double>
    ): Pair<Double, Double> {
        var (avgPrice, totalQty) = entry

        when (transfer.type) {
            "ROLL_OUT" -> {
                totalQty -= transfer.amount
            }

            "ROLL_IN" -> {
                if (totalQty == 0.0) {
                    avgPrice = transfer.price
                    totalQty = transfer.amount
                } else {
                    avgPrice = calculateAverageEntryPrice(
                        transfer.amount,
                        transfer.price,
                        totalQty,
                        avgPrice
                    )
                    totalQty += transfer.amount
                }
            }
        }

        return Pair(avgPrice, totalQty)
    }

    private fun calculateTrade(
        trade: Trade,
        entry: Pair<Double, Double>
    ): Pair<Double, Double> {
        var (avgPrice, totalQty) = entry

        if (trade.isBuyer && totalQty + trade.qty == .0) {
            totalQty = 0.0
            avgPrice = 0.0

        } else if (trade.isBuyer && totalQty + trade.qty > .0) {
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
            /*if (trade.symbol.contains("BONK")) {
                Log.e(
                    "GetEntryPriceUseCase",
                    "calculateTrade1: ${avgPrice.format(8)}, totalQty: ${totalQty.format(1)}, value: ${
                        (avgPrice * totalQty).format(
                            2
                        )
                    }"
                )
            }*/

        } else if (!trade.isBuyer && totalQty - trade.qty == .0) {
            totalQty = 0.0
            avgPrice = 0.0

        } else if (!trade.isBuyer && totalQty - trade.qty > .0) {
            // closing longs
            totalQty -= trade.qty
            /*if (trade.symbol.contains("BONK")) {
                Log.e(
                    "GetEntryPriceUseCase",
                    "calculateTrade2: ${avgPrice.format(8)}, totalQty: ${totalQty.format(1)}, value: ${
                        (avgPrice * totalQty).format(
                            2
                        )
                    }"
                )
            }*/

        } else if (!trade.isBuyer && totalQty - trade.qty < .0) {
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
            /*if (trade.symbol.contains("BONK")) {
                Log.e(
                    "GetEntryPriceUseCase",
                    "calculateTrade3: ${avgPrice.format(8)}, totalQty: ${totalQty.format(1)}, value: ${
                        (avgPrice * totalQty).format(
                            2
                        )
                    }"
                )
            }*/

        } else if (trade.isBuyer && totalQty + trade.qty < .0) {
            // closing shorts
            totalQty += trade.qty
            /*if (trade.symbol.contains("BONK")) {
                Log.e(
                    "GetEntryPriceUseCase",
                    "calculateTrade4: ${avgPrice.format(8)}, totalQty: ${totalQty.format(1)}, value: ${
                        (avgPrice * totalQty).format(
                            2
                        )
                    }"
                )
            }*/
        }

        return Pair(avgPrice, totalQty)
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

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}