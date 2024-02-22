package com.codeskraps.core.domain.usecases.trade

import com.codeskraps.core.client.BinanceClient
import javax.inject.Inject

class GetTradedSymbolsUseCase @Inject constructor(
    private val client: BinanceClient
) {
    operator fun invoke() = client.tradedSymbols()
}