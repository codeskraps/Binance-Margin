package com.codeskraps.core.domain.model

data class Asset(
    val asset: String,
    val free: Double,
    val locked: Double,
    val borrowed: Double,
    val interest: Double,
    val netAsset: Double
)