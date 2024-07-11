package com.codeskraps.core.domain.usecases.symbol

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.mappers.toCandle
import com.codeskraps.core.domain.model.Candle
import com.codeskraps.core.domain.model.Interval
import javax.inject.Inject

class GetCandlesUseCase @Inject constructor(
    private val client: BinanceClient
) {
    operator fun invoke(symbol: String, interval: Interval, limit: Int): List<Candle> {
        return runCatching {
            client.kLines(
                symbol = symbol,
                interval = interval.value,
                limit = limit
            ).map { it.toCandle() }
        }.getOrDefault(emptyList())
    }
}