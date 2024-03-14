package com.codeskraps.core.domain.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

enum class TradeType {
    LONG, SHORT
}

data class FinishTrade(
    val exitTime: Long,
    val entryTime: Long,
    val symbol: String,
    val pnl: Double,
    val pnlPercent: Double,
    val type: TradeType,
    val entryPrice: Double,
    val exitPrice: Double,
    val trades: Int
) {
    private val formatter by lazy { DateTimeFormatter.ofPattern("dd/MM HH:mm:ss") }

    val displayTime: String
        get() = runCatching {
            val instant = Instant.ofEpochMilli(exitTime)
            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            dateTime.format(formatter)
        }.getOrElse { "" }

    val tradeLength: String
        get() = runCatching {
            val differenceInMillis = exitTime - entryTime

            // Convert the difference to days, hours, minutes, and seconds
            val days = TimeUnit.MILLISECONDS.toDays(differenceInMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(differenceInMillis) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis) % 60

            // Construct the time difference string
            String.format("%d Days, %02d:%02d:%02d", days, hours, minutes, seconds)
        }.getOrElse { "" }

}
