package com.codeskraps.core.client.model

data class MarginAccountDto(
    val marginLevel: Double = .0,
    val totalAssetOfBtc: Double = .0,
    val totalLiabilityOfBtc: Double = .0,
    val totalNetAssetOfBtc: Double = .0,
    val borrowEnabled: Boolean = false,
    val CollateralMarginLevel: Double = .0,
    val TotalCollateralValueInUSDT: Double = .0,
    val tradeEnabled: Boolean = false,
    val transferEnabled: Boolean = false,
    val accountType: String = "",
    val userAssets: List<AssetDto> = emptyList()
)

data class AssetDto(
    val asset: String,
    val free: Double,
    val locked: Double,
    val borrowed: Double,
    val interest: Double,
    val netAsset: Double
)
