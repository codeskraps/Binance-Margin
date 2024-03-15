package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.domain.mappers.toTransfer
import com.codeskraps.core.domain.model.Transfer
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.core.realm.dao.TransferDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransfersUseCase @Inject constructor(
    private val transferDao: TransferDao
) {

    suspend operator fun invoke(symbol: String): List<Transfer> {
        return daoFlow(symbol)
    }

    private suspend fun daoFlow(symbol: String): List<Transfer> =
        transferDao.findAll()
            .filter { "${it.asset}${Constants.BASE_ASSET}" == symbol }
            .map { it.toTransfer() }
            .sortedBy { it.timestamp }
            .reversed()
}