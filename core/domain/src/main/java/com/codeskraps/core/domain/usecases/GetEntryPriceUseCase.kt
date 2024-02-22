package com.codeskraps.core.domain.usecases

import com.codeskraps.core.client.BinanceClient
import javax.inject.Inject

class GetEntryPriceUseCase @Inject constructor(
    private val client: com.codeskraps.core.client.BinanceClient
) {
    operator fun invoke(symbol: String) = client.entryPrice(symbol)
}