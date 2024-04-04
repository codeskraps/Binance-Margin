package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.domain.mappers.toEntryPriceEntity
import com.codeskraps.core.domain.model.EntryPrice
import com.codeskraps.core.realm.dao.EntryPriceDao
import javax.inject.Inject

class PutEntryPriceUseCase @Inject constructor(
    private val entryPriceDao: EntryPriceDao
) {
    suspend operator fun invoke(entryPrice: EntryPrice) {
        entryPriceDao.update(entryPrice.toEntryPriceEntity())
    }
}