package com.codeskraps.core.domain.usecases.trade

import android.util.Log
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.realm.dao.OrderDao
import javax.inject.Inject

class DeleteOrderUseCase @Inject constructor(
    private val orderDao: OrderDao
) {

    suspend operator fun invoke(order: Order) {
        runCatching {
            orderDao.deleteById(order.orderId, "orderId")
        }.onFailure { e ->
            Log.e("DeleteOrderUseCase", e.message.toString())
        }
    }
}