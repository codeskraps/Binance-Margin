package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.domain.mappers.toEntryPrice
import com.codeskraps.core.domain.model.EntryPrice
import com.codeskraps.core.realm.dao.EntryPriceDao
import javax.inject.Inject

class GetRealmEntryPriceUseCase @Inject constructor(
    private val entryPriceDao: EntryPriceDao
) {
    suspend operator fun invoke(symbol: String): EntryPrice? {
        return entryPriceDao.findById(symbol)?.toEntryPrice()
    }
}