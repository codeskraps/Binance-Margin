package com.codeskraps.core.domain.model

enum class PnLTimeType(val value: String) {
    DAY("1d"),
    WEEK("1w"),
    MONTH("1m"),
    YEAR("1y"),
    ALL("all")
}