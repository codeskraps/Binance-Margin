package com.codeskraps.core.domain.usecases.trade

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.mappers.toOrder
import com.codeskraps.core.domain.mappers.toOrderEntity
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.realm.dao.OrderDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val client: BinanceClient,
    private val orderDao: OrderDao
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(
        symbols: List<String>,
        network: (Boolean) -> Unit
    ): Flow<List<Order>> {
        return flowOf(clientFlow(symbols, network), daoFlow()).flattenMerge()
    }

    private fun clientFlow(symbols: List<String>, network: (Boolean) -> Unit): Flow<List<Order>> =
        flow {
            client.orders(symbols).let { orders ->
                orderDao.deleteAll()
                orderDao.updateAll(orders.map { it.toOrderEntity() })
                network(true)
            }
        }

    private suspend fun daoFlow(): Flow<List<Order>> = orderDao.stream().map { results ->
        results.list
    }.map { result ->
        result.map { it.toOrder() }.sortedBy { it.time }.reversed()
    }
}