package com.codeskraps.core.client.model

data class TradeDto(
    val symbol: String,
    val id: Long,
    val orderId: Long,
    val price: Double,
    val qty: Double,
    val quoteQty: Double,
    val commission: Double,
    val commissionAsset: String,
    val time: Long,
    val isBuyer: Boolean,
    val isMaker: Boolean,
    val isBestMatch: Boolean,
    val isIsolated: Boolean
)
