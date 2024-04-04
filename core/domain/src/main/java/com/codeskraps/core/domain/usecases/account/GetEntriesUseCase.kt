package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.model.Entry
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.model.Transfer
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetEntriesUseCase @Inject constructor(
    private val tradesUseCase: GetTradesUseCase,
    private val transfersUseCase: GetTransfersUseCase,
) {
    suspend operator fun invoke(symbol: String): List<Entry> {
        return coroutineScope {
            val deferredResults = listOf(
                async { tradesUseCase(symbol) },
                async { transfersUseCase(symbol) }
            )

            val results = awaitAll(*deferredResults.toTypedArray())

            val trades: List<Entry> = (results[0] as List<*>)
                .filterIsInstance<Trade>()
                .filter { it.symbol == symbol }
            val transfers: List<Entry> = (results[1] as List<*>)
                .filterIsInstance<Transfer>()
                .filter { "${it.asset}${BinanceClient.BASE_ASSET}" == symbol }

            return@coroutineScope listOf(trades, transfers).flatten().sortedBy { it.time() }
        }
    }
}