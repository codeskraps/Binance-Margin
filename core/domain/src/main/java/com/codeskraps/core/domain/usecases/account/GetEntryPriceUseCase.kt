package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceClient
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

            val trades = (results[0] as List<*>)
                .filterIsInstance<Trade>()
                .filter { it.symbol == symbol }
                .reversed()
            val transfers = (results[1] as List<*>).filterIsInstance<Transfer>()

            var avgPrice = .0
            var totalQty = .0

            transfers.filter { transfer ->
                "${transfer.asset}${BinanceClient.BASE_ASSET}" == symbol
            }.forEach { transfer ->
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

            return@coroutineScope avgPrice
        }
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