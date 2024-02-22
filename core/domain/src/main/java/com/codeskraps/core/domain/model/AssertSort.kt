package com.codeskraps.core.domain.model

enum class AssertSort(val value: String) {
    Value("Value"),
    Invested("Invested"),
    PnL("PnL"),
    PnLPercent("PnL %");

    companion object {
        fun fromString(value: String): AssertSort {
            return when (value) {
                "Value" -> Value
                "Invested" -> Invested
                "PnL" -> PnL
                "PnL %" -> PnLPercent
                else -> Value
            }
        }
    }
}