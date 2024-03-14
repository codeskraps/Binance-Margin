package com.codeskraps.core.client.model

data class InterestHistoryDto(
    val total: Int,
    val rows: List<InterestDto>
)

data class InterestDto(
    val txId: Long,
    val interestAccuredTime: Long,
    val asset: String,
    val rawAsset: String,
    val principal: String,
    val interest: String,
    val interestRate: String,
    val type: String
)
