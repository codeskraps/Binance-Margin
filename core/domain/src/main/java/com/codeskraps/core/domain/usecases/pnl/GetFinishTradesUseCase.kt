package com.codeskraps.core.domain.usecases.pnl

import com.codeskraps.core.domain.mappers.toFinishTrade
import com.codeskraps.core.domain.model.FinishTrade
import com.codeskraps.core.realm.dao.FinishTradeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFinishTradesUseCase @Inject constructor(
    private val finishTradeDao: FinishTradeDao
) {
    suspend operator fun invoke(): Flow<List<FinishTrade>> =
        finishTradeDao.stream().map { results ->
            results.list
        }.map { result ->
            result.map { it.toFinishTrade() }.sortedBy { it.exitTime }.reversed()
        }
}