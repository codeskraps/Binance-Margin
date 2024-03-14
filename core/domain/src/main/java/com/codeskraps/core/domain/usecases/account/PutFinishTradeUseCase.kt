package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.domain.mappers.toFinishedTradeEntity
import com.codeskraps.core.domain.model.FinishTrade
import com.codeskraps.core.realm.dao.FinishTradeDao
import javax.inject.Inject

class PutFinishTradeUseCase @Inject constructor(
    private val finishedTradeDao: FinishTradeDao
) {
    suspend operator fun invoke(finishTrade: FinishTrade) {
        val entity = finishTrade.toFinishedTradeEntity()
        val stored = finishedTradeDao.findById(entity.id)

        if (stored == null) {
            finishedTradeDao.insert(entity)
        }
    }
}