package com.codeskraps.core.domain.model

enum class Interval(val value: String) {
    MINUTE("1m"),
    HOURLY("1h"),
    FOUR_HOURLY("4h"),
    DAILY("1d"),
    WEEKLY("1w");

    companion object {
        fun fromString(value: String): Interval {
            return when (value) {
                "1m" -> MINUTE
                "1h" -> HOURLY
                "4h" -> FOUR_HOURLY
                "1w" -> WEEKLY
                else -> DAILY
            }
        }
    }
}