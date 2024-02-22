package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.mappers.toTicker
import javax.inject.Inject

class GetTickersUseCase @Inject constructor(
    private val client: BinanceClient
) {
    operator fun invoke(symbols: ArrayList<String>) =
        client.tickerSymbol(symbols).map { it.toTicker() }
}