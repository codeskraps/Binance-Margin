package com.codeskraps.core.client.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
) {
    private val formatter by lazy { DateTimeFormatter.ofPattern("dd/MM HH:mm:ss") }

    val displayTime: String
        get() = runCatching {
            val instant = Instant.ofEpochMilli(time)
            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            dateTime.format(formatter)
        }.getOrElse { "" }
}
