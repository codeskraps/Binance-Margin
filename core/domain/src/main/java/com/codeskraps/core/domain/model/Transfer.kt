package com.codeskraps.core.domain.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
) {
    private val formatter by lazy { DateTimeFormatter.ofPattern("dd/MM HH:mm:ss") }

    val displayTime: String
        get() = runCatching {
            val instant = Instant.ofEpochMilli(timestamp)
            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            dateTime.format(formatter)
        }.getOrElse { "" }
}
