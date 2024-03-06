package com.codeskraps.core.domain.model

data class Trade(
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
) : Entry() {

    override fun time(): Long = time
}
