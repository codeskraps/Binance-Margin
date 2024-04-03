package com.codeskraps.core.domain.model

data class WatchlistItem(
    val symbol: String,
    val price: Double,
    val priceChange: Double,
    val priceChangePercent: Double
)
