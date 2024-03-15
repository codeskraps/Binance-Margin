package com.codeskraps.core.domain.usecases.symbol

import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.domain.model.Interval
import javax.inject.Inject

class GetChartTimeUseCase @Inject constructor(
    private val store: BinanceStore
) {
    operator fun invoke(): Interval {
        return Interval.fromString(store.chartTimeType)
    }
}