package com.codeskraps.core.domain.mappers

import com.codeskraps.core.client.model.CandleDto
import com.codeskraps.core.domain.model.Candle

fun CandleDto.toCandle() = Candle(
    closeTime = closeTime.toFloat(),
    open = open.toFloat(),
    high = high.toFloat(),
    low = low.toFloat(),
    close = close.toFloat()
)