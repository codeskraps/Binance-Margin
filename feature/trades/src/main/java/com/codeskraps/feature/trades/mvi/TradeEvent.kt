package com.codeskraps.feature.trades.mvi

import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.model.Trade

sealed interface TradeEvent {
    data object Resume : TradeEvent
    data class LoadedTrades(val trades: List<Trade>) : TradeEvent
    data class LoadedOrders(val orders: List<Order>) : TradeEvent
    data class TradesSelection(val symbol: String) : TradeEvent
    data object StopLoading : TradeEvent
}