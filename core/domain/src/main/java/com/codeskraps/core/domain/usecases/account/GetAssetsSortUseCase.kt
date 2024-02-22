package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.domain.model.AssertSort
import javax.inject.Inject

class GetAssetsSortUseCase @Inject constructor(
    private val store: BinanceStore
) {
    operator fun invoke(): AssertSort {
        return AssertSort.fromString(store.assetsSort)
    }
}