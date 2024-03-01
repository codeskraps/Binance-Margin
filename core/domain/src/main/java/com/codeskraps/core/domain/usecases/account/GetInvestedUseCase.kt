package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.domain.usecases.trade.GetTransfersUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetInvestedUseCase @Inject constructor(
    private val transfersUseCase: GetTransfersUseCase
) {
    suspend operator fun invoke(): Double {
        var totalInvested = 0.0
        transfersUseCase { }.first().forEach { transfer ->
            when (transfer.type) {
                "ROLL_OUT" -> {
                    totalInvested -= transfer.amount * transfer.price
                }

                "ROLL_IN" -> {
                    totalInvested += transfer.amount * transfer.price
                }
            }
        }
        return totalInvested
    }
}