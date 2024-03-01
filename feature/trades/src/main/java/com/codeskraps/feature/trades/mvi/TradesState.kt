package com.codeskraps.feature.trades.mvi

import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.model.Transfer

data class TradesState(
    val isLoading: Boolean,
    val trades: List<Trade>,
    val tradeSymbols: Set<String>,
    val tradeSelection: String,
    val orders: List<Order>,
    val transfers: List<Transfer>
) {
    companion object {
        val initial = TradesState(
            isLoading = false,
            trades = emptyList(),
            tradeSymbols = emptySet(),
            tradeSelection = "All Trades",
            orders = emptyList(),
            transfers = emptyList()
        )
    }
}
