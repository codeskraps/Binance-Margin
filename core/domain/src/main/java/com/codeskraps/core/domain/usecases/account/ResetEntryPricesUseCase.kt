package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.realm.dao.EntryPriceDao
import com.codeskraps.core.realm.model.EntryPriceEntity
import javax.inject.Inject

class ResetEntryPricesUseCase @Inject constructor(
    private val entryPriceDao: EntryPriceDao,
    private val getEntriesUseCase: GetEntriesUseCase,
    private val store: BinanceStore
) {
    suspend operator fun invoke(symbols: List<String>) {
        val tradedAssets = store.tradedAssets.toMutableList()
        tradedAssets.removeAll(symbols)
        tradedAssets.forEach { symbol ->

            entryPriceDao.findById(symbol)?.let { entryPrice ->
                if (entryPrice.price != .0) {
                    val entries = getEntriesUseCase(symbol)

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
}