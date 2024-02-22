package com.codeskraps.binance.ui.mvi

data class MainActivityState(
    val hasApiKey: Boolean
) {
    companion object {
        val Initial = MainActivityState(
            hasApiKey = false
        )
    }
}
