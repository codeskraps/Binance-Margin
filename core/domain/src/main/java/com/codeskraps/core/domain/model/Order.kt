package com.codeskraps.core.domain.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Order(
    val symbol: String,
    val orderId: Long,
    val clientOrderId: String,
    val price: String,
    val origQty: String,
    val executedQty: String,
    val cummulativeQuoteQty: String,
    val status: String,
    val timeInForce: String,
    val type: String,
    val side: String,
    val stopPrice: String,
    val icebergQty: String,
    val time: Long,
    val updateTime: Long,
    val isWorking: Boolean,
    val isIsolated: Boolean,
    val selfTradePreventionMode: String
) {
    private val formatter by lazy { DateTimeFormatter.ofPattern("dd/MM HH:mm:ss") }

    val displayTime: String
        get() = runCatching {
            val instant = Instant.ofEpochMilli(time)
            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            dateTime.format(formatter)
        }.getOrElse { "" }
}
