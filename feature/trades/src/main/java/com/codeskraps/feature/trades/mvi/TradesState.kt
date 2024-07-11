package com.codeskraps.feature.trades.mvi

import androidx.paging.PagingData
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.model.Transfer

data class TradesState(
    val isLoading: Boolean,
    val trades: List<Trade>,
    val tradeSymbols: Set<String>,
    val tradeSelection: Int,
    val orders: List<Order>,
    val ordersSymbols: Set<String>,
    val ordersSelection: Int,
    val transfers: List<Transfer>
) {
    companion object {
        val initial = TradesState(
            isLoading = false,
            trades = emptyList(),
            tradeSymbols = emptySet(),
            tradeSelection = 0,
            orders = emptyList(),
            ordersSymbols = emptySet(),
            ordersSelection = 0,
            transfers = emptyList()
        )
    }
}
