package com.codeskraps.core.domain.usecases.trade

import com.codeskraps.core.domain.mappers.toTransferEntity
import com.codeskraps.core.domain.model.Transfer
import com.codeskraps.core.realm.dao.TransferDao
import javax.inject.Inject

class UpdatePriceUseCase @Inject constructor(
    private val transferDao: TransferDao
) {
    suspend operator fun invoke(transfer: Transfer) {
        transferDao.update(transfer.toTransferEntity())
    }
}