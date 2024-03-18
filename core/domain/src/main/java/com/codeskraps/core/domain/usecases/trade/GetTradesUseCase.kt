package com.codeskraps.core.domain.usecases.trade

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.mappers.toTrade
import com.codeskraps.core.domain.mappers.toTradeEntity
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.realm.dao.TradeDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTradesUseCase @Inject constructor(
    private val tradedSymbolsUseCase: GetTradedSymbolsUseCase,
    private val client: BinanceClient,
    private val tradeDao: TradeDao
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(
        network: (Boolean) -> Unit
    ): Flow<List<Trade>> {
        return flowOf(clientFlow(network), daoFlow()).flattenMerge()
    }

    private fun clientFlow(network: (Boolean) -> Unit): Flow<List<Trade>> =
        flow {
            val symbols = tradedSymbolsUseCase()
            client.trades(symbols).let { trades ->
                tradeDao.updateAll(trades.map { it.toTradeEntity() })
                network(true)
            }
        }

    private suspend fun daoFlow(): Flow<List<Trade>> = tradeDao.stream().map { results ->
        results.list
    }.map { result ->
        result.map { it.toTrade() }.sortedBy { it.time }.reversed()
    }
}