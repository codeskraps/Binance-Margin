package com.codeskraps.binance.navigation

sealed class Screen(val route: String) {
    data object SetUp : Screen("set_up")
    data object AccountTrade : Screen("account_trade")
    data object Account : Screen("account")
    data object Trade : Screen("trade")
    data object PnL : Screen("pnl")
    data object Setting : Screen("setting")
}