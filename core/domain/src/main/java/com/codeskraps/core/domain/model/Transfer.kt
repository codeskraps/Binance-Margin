package com.codeskraps.core.domain.model

data class Transfer(
    val timestamp: Long,
    val asset: String,
    val amount: Double,
    val type: String,
    val status: String,
    val txId: Long,
    val transFrom: String,
    val transTo: String,
    val price: Double
) : Entry() {

    override fun time(): Long = timestamp
}
