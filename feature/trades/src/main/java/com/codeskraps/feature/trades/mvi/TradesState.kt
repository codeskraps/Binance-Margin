package com.codeskraps.feature.trades.mvi

import com.codeskraps.core.client.model.Order
import com.codeskraps.core.client.model.Trade

data class TradesState(
    val isLoading: Boolean,
    val trades: List<com.codeskraps.core.client.model.Trade>,
    val orders: List<com.codeskraps.core.client.model.Order>
) {
    companion object {
        val initial = TradesState(
            isLoading = false,
            trades = emptyList(),
            orders = emptyList()
        )
    }
}
