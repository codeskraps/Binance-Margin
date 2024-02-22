package com.codeskraps.core.domain.usecases

import com.codeskraps.core.client.BinanceClient
import javax.inject.Inject

class GetInvestedUseCase @Inject constructor(
    private val client: com.codeskraps.core.client.BinanceClient
) {
    operator fun invoke() = client.invested()
}