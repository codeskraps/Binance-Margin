package com.codeskraps.core.domain.usecases.account

import android.util.Log
import com.codeskraps.core.domain.mappers.toPnL
import com.codeskraps.core.domain.model.PnL
import com.codeskraps.core.domain.model.PnLTimeType
import com.codeskraps.core.realm.dao.PnLDailyDao
import com.codeskraps.core.realm.dao.PnLHourlyDao
import com.codeskraps.core.realm.model.PnLEntity
import javax.inject.Inject

class GetPnLUseCase @Inject constructor(
    private val pnLHourlyDao: PnLHourlyDao,
    private val pnLDailyDao: PnLDailyDao
) {
    companion object {
        private val TAG = GetPnLUseCase::class.java.simpleName
        private const val TWENTY_FOUR_HOUR_IN_MILLIS = 24 * 60 * 60 * 1000L
        private const val ONE_WEEK_IN_MILLIS = 7 * TWENTY_FOUR_HOUR_IN_MILLIS
        private const val ONE_MONTH_IN_MILLIS = 30 * TWENTY_FOUR_HOUR_IN_MILLIS
        private const val ONE_YEAR_IN_MILLIS = 365 * TWENTY_FOUR_HOUR_IN_MILLIS
    }

    suspend operator fun invoke(time: PnLTimeType): List<PnL> {
        val result = runCatching {
            val entries = when (time) {
                PnLTimeType.DAY -> {
                    pnLHourlyDao.findByTime(System.currentTimeMillis() - TWENTY_FOUR_HOUR_IN_MILLIS)
                }

                PnLTimeType.WEEK -> {
                    pnLHourlyDao.findByTime(System.currentTimeMillis() - ONE_WEEK_IN_MILLIS)
                }

                PnLTimeType.MONTH -> {
                    pnLDailyDao.findByTime(System.currentTimeMillis() - ONE_MONTH_IN_MILLIS)
                }

                PnLTimeType.YEAR -> {
                    pnLDailyDao.findByTime(System.currentTimeMillis() - ONE_YEAR_IN_MILLIS)
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