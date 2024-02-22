package com.codeskraps.binance.ui.mvi

sealed interface MainActivityEvent {
    data class HasApiKey(val hasApiKey: Boolean) : MainActivityEvent
    data object CheckApiKey : MainActivityEvent
}