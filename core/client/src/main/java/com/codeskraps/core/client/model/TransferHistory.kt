package com.codeskraps.core.client.model

data class TransferHistory(
    val total: Int = 0,
    val rows: List<Transfer> = emptyList()
)

data class Transfer(
    val timestamp: Long,
    val asset: String,
    val amount: Double,
    val type: String,
    val status: String,
    val txId: Long,
    val transFrom: String,
    val transTo: String
)
