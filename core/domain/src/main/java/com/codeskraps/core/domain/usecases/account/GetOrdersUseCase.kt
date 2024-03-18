package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.domain.mappers.toOrder
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.realm.dao.OrderDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val orderDao: OrderDao
) {
    suspend operator fun invoke(): List<Order> {
        return daoFlow()
    }

    private suspend fun daoFlow(): List<Order> = orderDao.findAll().map { order ->
        order.toOrder()
    }.sortedBy { it.time }.reversed()
}