package com.codeskraps.core.domain.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.client.Response
import com.codeskraps.core.domain.mappers.toOrderEntity
import com.codeskraps.core.domain.pnl.PnLWorker
import com.codeskraps.core.domain.usecases.trade.GetOrdersUseCase
import com.codeskraps.core.realm.dao.OrderDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class OrdersWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val store: BinanceStore,
    private val client: BinanceClient,
    private val orderDao: OrderDao
) : CoroutineWorker(context, params) {
    companion object {
        private val TAG = OrdersWorker::class.java.simpleName
        private const val SIX_HOURS = 1000 * 60 * 60 * 6
    }

    override suspend fun doWork(): Result {
        val result = runCatching {
            client.tradedSymbols().forEach { symbol ->
                processSymbols(symbol)
            }
            if (store.lastOrderChecked - System.currentTimeMillis() > SIX_HOURS) {
                client.allSymbols().forEach { symbol ->
                    processSymbols(symbol)
                    store.lastOrderChecked = System.currentTimeMillis()
                }
            }
        }
        return if (result.isSuccess) {
            Result.success()
        } else {
            result.onFailure { e -> Log.e(TAG, "doWork: $e") }
            Result.retry()
        }
    }

    private suspend fun processSymbols(symbol: String) {
        var retry = true

        while (retry) {
            when (val result = client.orders(symbol)) {
                is Response.Success -> {
                    orderDao.findAll().filter { it.symbol == symbol }.forEach {
                        orderDao.delete(it)
                    }
                    orderDao.insertAll(result.data.map { it.toOrderEntity() })
                    retry = false
                }

                is Response.Failure -> {
                    when (result.error) {
                        BinanceClient.OrderError.LIMIT_REACHED -> {
                            Log.e(TAG, "doWork: ${result.error}")
                            retry = true
                            delay(1000 * 60)
                        }

                        BinanceClient.OrderError.BAD_SYMBOL -> {
                            Log.e(TAG, "doWork: ${result.error}")
                            retry = false
                        }

                        BinanceClient.OrderError.UNKNOWN -> {
                            Log.e(TAG, "doWork: ${result.error}")
                            retry = false
                        }
                    }
                }
            }
        }
    }
}