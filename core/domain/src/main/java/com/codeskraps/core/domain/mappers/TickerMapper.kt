package com.codeskraps.core.domain.mappers

import com.codeskraps.core.client.model.TickerDto
import com.codeskraps.core.domain.model.Ticker

fun TickerDto.toTicker(): Ticker {
    return Ticker(
        symbol = symbol,
        price = price
    )
}