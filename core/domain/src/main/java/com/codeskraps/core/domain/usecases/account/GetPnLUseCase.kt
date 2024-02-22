package com.codeskraps.core.domain.usecases.account

import android.util.Log
import com.codeskraps.core.domain.mappers.toPnL
import com.codeskraps.core.domain.model.PnL
import com.codeskraps.core.domain.model.PnLTimeType
import com.codeskraps.core.realm.PnLDailyDao
import com.codeskraps.core.realm.PnLHourlyDao
import com.codeskraps.core.realm.model.PnLEntity
import javax.inject.Inject

class GetPnLUseCase @Inject constructor(
    private val pnLHourlyDao: PnLHourlyDao,
    private val pnLDailyDao: PnLDailyDao
) {
    companion object {
        private val TAG = GetPnLUseCase::class.java.simpleName
        private const val twentyFourHoursInMillis = 24 * 60 * 60 * 1000L
        private const val oneWeekInMillis = 7 * twentyFourHoursInMillis
        private const val oneMonthInMillis = 30 * twentyFourHoursInMillis
        private const val oneYearInMillis = 365 * twentyFourHoursInMillis
    }

    suspend operator fun invoke(time: PnLTimeType): List<PnL> {
        val result = runCatching {
            val entries = when (time) {
                PnLTimeType.DAY -> {
                    pnLHourlyDao.findByTime(System.currentTimeMillis() - twentyFourHoursInMillis)
                }

                PnLTimeType.WEEK -> {
                    pnLHourlyDao.findByTime(System.currentTimeMillis() - oneWeekInMillis)
                }

                PnLTimeType.MONTH -> {
                    pnLDailyDao.findByTime(System.currentTimeMillis() - oneMonthInMillis)
                }

                PnLTimeType.YEAR -> {
                    pnLDailyDao.findByTime(System.currentTimeMillis() - oneYearInMillis)
                }

                PnLTimeType.ALL -> pnLDailyDao.findAll()
            }.sortedBy { it.time() }

            val divider = when (time) {
                PnLTimeType.DAY -> 100
                PnLTimeType.WEEK -> 12
                PnLTimeType.MONTH,
                PnLTimeType.YEAR,
                PnLTimeType.ALL -> 30
            }

            val stepSize = calculateStepSize(entries, divider)

            if (stepSize == 0) return entries.map { it.toPnL() }

            val evenlySpacedElements = mutableListOf<PnLEntity>()

            for (i in entries.indices step stepSize) {
                evenlySpacedElements.add(entries[i])
            }

            evenlySpacedElements.map { it.toPnL() }
        }

        return if (result.isSuccess) {
            result.getOrElse { emptyList() }
        } else {
            result.onFailure { e -> Log.e(TAG, "invoke: $e") }
            emptyList()
        }
    }

    private fun calculateStepSize(entries: List<PnLEntity>, divider: Int): Int {
        return (entries.size + (divider - 1)) / divider
    }
}