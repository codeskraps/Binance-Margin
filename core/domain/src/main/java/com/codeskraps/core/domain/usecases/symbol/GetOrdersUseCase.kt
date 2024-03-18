package com.codeskraps.core.domain.usecases.symbol

import com.codeskraps.core.domain.mappers.toOrder
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.realm.dao.OrderDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val orderDao: OrderDao
) {
    suspend operator fun invoke(
        symbol: String
    ): Flow<List<Order>> {
        return daoFlow(symbol)
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