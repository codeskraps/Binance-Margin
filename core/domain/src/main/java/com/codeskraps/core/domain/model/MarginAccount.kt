package com.codeskraps.core.domain.model

data class MarginAccount(
    val marginLevel: Double = .0,
    val totalAssetOfBtc: Double = .0,
    val totalLiabilityOfBtc: Double = .0,
    val totalNetAssetOfBtc: Double = .0,
    val borrowEnabled: Boolean = false,
    val collateralMarginLevel: Double = .0,
    val totalCollateralValueInUSDT: Double = .0,
    val tradeEnabled: Boolean = false,
    val transferEnabled: Boolean = false,
    val accountType: String = "",
    val userAssets: List<Asset> = emptyList()
)