package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.domain.mappers.toTrade
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.realm.dao.TradeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTradesUseCase @Inject constructor(
    private val tradeDao: TradeDao
) {

    suspend operator fun invoke(symbol: String): List<Trade> {
        return daoFlow(symbol)
    }

    private suspend fun daoFlow(symbol: String): List<Trade> =
        tradeDao.findAll()
            .filter { it.symbol == symbol }
            .map { it.toTrade() }
            .sortedBy { it.time }
            .reversed()
}