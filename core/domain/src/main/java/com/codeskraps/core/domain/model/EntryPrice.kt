package com.codeskraps.core.domain.model

data class EntryPrice(
    val symbol: String,
    val price: Double,
    val lastTrade: Long
)
