package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.domain.model.Entry
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.model.Transfer
import com.codeskraps.core.realm.dao.EntryPriceDao
import com.codeskraps.core.realm.model.EntryPriceEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ResetEntryPricesUseCase @Inject constructor(
    private val entryPriceDao: EntryPriceDao,
    private val tradesUseCase: GetTradesUseCase,
    private val transfersUseCase: GetTransfersUseCase,
    private val store: BinanceStore
) {
    suspend operator fun invoke(symbols: List<String>) {
        val tradedAssets = store.tradedAssets.toMutableList()
        tradedAssets.removeAll(symbols)
        tradedAssets.forEach { symbol ->

            entryPriceDao.findById(symbol)?.let { entryPrice ->
                if (entryPrice.price != .0) {
                    val entries = getEntries(symbol)

                    entryPriceDao.update(
                        EntryPriceEntity(
                            symbol = symbol,
                            price = .0,
                            lastTrade = entries.lastOrNull()?.time() ?: System.currentTimeMillis(),
                        )
                    )
                }
            }
        }
    }

    private suspend fun getEntries(symbol: String): List<Entry> {
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