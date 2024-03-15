package com.codeskraps.feature.account.mvi

sealed interface AccountAction {
    data class OpenSymbol(val symbol: String, val entry: Double) : AccountAction
}