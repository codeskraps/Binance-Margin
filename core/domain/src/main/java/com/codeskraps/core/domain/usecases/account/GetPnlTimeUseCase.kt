package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.domain.model.PnLTimeType
import javax.inject.Inject

class GetPnlTimeUseCase @Inject constructor(
    private val store: BinanceStore
) {
    operator fun invoke(): PnLTimeType {
        return PnLTimeType.fromString(store.pnlTimeType)
    }
}