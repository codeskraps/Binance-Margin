package com.codeskraps.core.domain.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.mappers.toOrderEntity
import com.codeskraps.core.domain.pnl.PnLWorker
import com.codeskraps.core.domain.usecases.trade.GetOrdersUseCase
import com.codeskraps.core.realm.dao.OrderDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class OrdersWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val client: BinanceClient,
    private val orderDao: OrderDao
) : CoroutineWorker(context, params) {
    companion object {
        private val TAG = OrdersWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        val result = runCatching {
            client.orders().let { orders ->
                orderDao.deleteAll()
                orderDao.insertAll(orders.map { it.toOrderEntity() })
            }
        }
        return if (result.isSuccess) {
            Result.success()
        } else {
            result.onFailure { e -> Log.e(TAG, "doWork: $e") }
            Result.retry()
        }
    }
}