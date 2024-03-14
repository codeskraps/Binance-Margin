package com.codeskraps.core.domain.pnl

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.usecases.account.GetInvestedUseCase
import com.codeskraps.core.realm.dao.PnLDailyDao
import com.codeskraps.core.realm.dao.PnLHourlyDao
import com.codeskraps.core.realm.model.PnLHourlyEntity
import com.codeskraps.core.realm.model.PnlDailyEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

@HiltWorker
class PnLWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val client: BinanceClient,
    private val investedUseCase: GetInvestedUseCase,
    private val pnLHourlyDao: PnLHourlyDao,
    private val pnLDailyDao: PnLDailyDao
) : CoroutineWorker(context, params) {

    companion object {
        private val TAG = PnLWorker::class.java.simpleName
        private const val twentyFourHoursInMillis = 24 * 60 * 60 * 1000L
        private const val oneMonthInMillis = 30 * twentyFourHoursInMillis
    }

    override suspend fun doWork(): Result {
        //Log.e(TAG, "doWork")
        val result = runCatching {
            client.marginAccount()?.let { account ->
                runBlocking {
                    val invested = investedUseCase()
                    val symbols = arrayListOf("BTC${BinanceClient.BASE_ASSET}")
                    val btcPrice = client.tickerSymbol(symbols)

                    //Log.e(TAG, "doWork: $account")

                    pnLHourlyDao.update(
                        PnLHourlyEntity(
                            id = hourlyId(),
                            time = roundToHour(),
                            totalAssetOfUSDT = account.totalNetAssetOfBtc * btcPrice[0].price,
                            invested = invested,
                        )
                    )
                    pnLHourlyDao.deleteOlder(oneMonthInMillis)

                    pnLDailyDao.update(
                        PnlDailyEntity(
                            id = dailyId(),
                            time = roundToHour(),
                            totalAssetOfUSDT = account.totalNetAssetOfBtc * btcPrice[0].price,
                            invested = invested,
                        )
                    )
                }
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

    private fun hourlyId(): Long {
        return "${LocalDate.now().dayOfWeek.value.toLong()}${roundToHour() / 1000}".toLong()
    }

    private fun dailyId(): Long {
        return if (LocalDate.now().dayOfYear < 10) {
            "${LocalDate.now().year}00${LocalDate.now().dayOfYear}".toLong()
        } else if (LocalDate.now().dayOfYear < 100) {
            "${LocalDate.now().year}0${LocalDate.now().dayOfYear}".toLong()
        } else "${LocalDate.now().year}${LocalDate.now().dayOfYear}".toLong()
    }
}