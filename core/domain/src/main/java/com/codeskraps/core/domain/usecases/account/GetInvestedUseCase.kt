package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceClient
import javax.inject.Inject

class GetInvestedUseCase @Inject constructor(
    private val client: BinanceClient
) {
    operator fun invoke() = client.invested()
}