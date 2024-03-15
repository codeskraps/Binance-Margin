package com.codeskraps.core.domain.usecases.symbol

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
        symbol: String,
        network: (Boolean) -> Unit
    ): Flow<List<Order>> {
        return flowOf(clientFlow(symbol, network), daoFlow(symbol)).flattenMerge()
    }

    private fun clientFlow(symbol: String, network: (Boolean) -> Unit): Flow<List<Order>> =
        flow {
            client.orders(symbol).let { orders ->
                orderDao.findAll()
                    .filter { it.symbol == symbol }
                    .forEach { orderDao.delete(it) }
                orderDao.insertAll(orders.map { it.toOrderEntity() })
                network(true)
            }
        }

    private suspend fun daoFlow(symbol: String): Flow<List<Order>> =
        orderDao.stream().map { results ->
            results.list
        }.map { result ->
            result
                .filter { it.symbol == symbol }
                .map { it.toOrder() }
                .sortedBy { it.time }
                .reversed()
        }
}