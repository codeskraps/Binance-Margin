package com.codeskraps.core.domain.pnl

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.realm.PnLDao
import com.codeskraps.core.realm.model.PnLEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PnLWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val client: BinanceClient,
    private val pnLDao: PnLDao
) : CoroutineWorker(context, params) {

    companion object {
        private val TAG = PnLWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        val result = runCatching {
            client.marginAccount()?.let { account ->
                val invested = client.invested()
                val symbols = arrayListOf("BTC${BinanceClient.BASE_ASSET}")
                val btcPrice = client.tickerSymbol(symbols)

                pnLDao.update(
                    PnLEntity(
                        time = roundToHour(),
                        totalAssetOfUSDT = account.totalNetAssetOfBtc * btcPrice[0].price,
                        invested = invested,
                    )
                )
            }

            PnLTaskReceiver.setPnLAlarm(applicationContext)
        }

        return if (result.isSuccess) {
            Result.success()
        } else {
            result.onFailure { e -> Log.e(TAG, "doWork: $e") }
            Result.retry()
        }
    }

    private fun roundToHour(): Long {
        val currentTimeMillis = System.currentTimeMillis()
        val remainder = currentTimeMillis % (3600 * 1000)
        return if (remainder <= (1800 * 1000)) {
            // Round down to the nearest hour
            currentTimeMillis - remainder
        } else {
            // Round up to the nearest hour
            currentTimeMillis + (3600 * 1000 - remainder)
        }
    }
}