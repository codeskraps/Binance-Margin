package com.codeskraps.core.domain.model

enum class PnLTimeType(val value: String) {
    DAY("1d"),
    WEEK("1w"),
    MONTH("1m"),
    YEAR("1y"),
    ALL("all");

    companion object {
        fun fromString(value: String): PnLTimeType {
            return when (value) {
                "1d" -> DAY
                "1w" -> WEEK
                "1m" -> MONTH
                "1y" -> YEAR
                "all" -> ALL
                else -> DAY
            }
        }
    }
}