package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.domain.model.PnLTimeType
import javax.inject.Inject

class PutPnlTimeUseCase @Inject constructor(
    private val store: BinanceStore
) {
    operator fun invoke(pnLTimeType: PnLTimeType) {
        store.pnlTimeType = pnLTimeType.value
    }
}