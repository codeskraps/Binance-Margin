package com.codeskraps.binance

sealed class Screen(val route: String) {
    data object Account : Screen("account")
    data object Trade : Screen("trade")
}