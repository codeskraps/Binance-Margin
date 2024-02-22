package com.codeskraps.core.domain.mappers

import com.codeskraps.core.domain.model.PnL
import com.codeskraps.core.realm.model.PnLEntity

fun PnLEntity.toPnL() = PnL(
    time = time,
    invested = invested,
    totalAssetOfUSDT = totalAssetOfUSDT
)