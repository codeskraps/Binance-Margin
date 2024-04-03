package com.codeskraps.core.domain.navigation

sealed class Screen(val route: String) {
    data object SetUp : Screen("set_up")
    data object AccountTrade : Screen("account_trade")
    data object Account : Screen("account")
    data object Watchlist : Screen("watchlist")
    data object PnL : Screen("pnl")
    data object Trade : Screen("trade")
    data object Symbol : Screen("symbol/{symbol}/{entry}") {
        fun createRoute(symbol: String, entry: Double) = "symbol/$symbol/$entry"
    }

    data object Setting : Screen("setting")
}