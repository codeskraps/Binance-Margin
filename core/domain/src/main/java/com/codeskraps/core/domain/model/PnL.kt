package com.codeskraps.core.domain.model

data class PnL(
    val time: Long,
    val invested: Double,
    val totalAssetOfUSDT: Double
) {
    val pnl: Double = totalAssetOfUSDT - invested
}
