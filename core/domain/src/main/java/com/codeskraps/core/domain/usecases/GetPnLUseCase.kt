package com.codeskraps.core.domain.usecases

import android.util.Log
import com.codeskraps.core.domain.mappers.toPnL
import com.codeskraps.core.domain.model.PnL
import com.codeskraps.core.domain.model.PnLTimeType
import javax.inject.Inject

class GetPnLUseCase @Inject constructor(
    private val pnLDao: com.codeskraps.core.realm.PnLDao
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
                    pnLDao.findByTime(System.currentTimeMillis() - twentyFourHoursInMillis)
                }

                PnLTimeType.WEEK -> {
                    pnLDao.findByTime(System.currentTimeMillis() - oneWeekInMillis)
                }

                PnLTimeType.MONTH -> {
                    pnLDao.findByTime(System.currentTimeMillis() - oneMonthInMillis)
                }

                PnLTimeType.YEAR -> {
                    pnLDao.findByTime(System.currentTimeMillis() - oneYearInMillis)
                }

                PnLTimeType.ALL -> pnLDao.findAll()
            }.sortedBy { it.time }

            val stepSize = entries.size / 24

            if (stepSize == 0) return entries.map { it.toPnL() }

            val evenlySpacedElements = mutableListOf<com.codeskraps.core.realm.model.PnLEntity>()

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
}