package com.codeskraps.feature.trades.mvi

import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.model.Transfer

sealed interface TradeEvent {
    data object Resume : TradeEvent
    data class LoadedTrades(val trades: List<Trade>) : TradeEvent
    data class LoadedOrders(val orders: List<Order>) : TradeEvent
    data class LoadedTransfers(val transfers: List<Transfer>) : TradeEvent
    data class TradesSelection(val symbol: String) : TradeEvent
    data object StopLoading : TradeEvent
    data class PriceUpdate(val transfer: Transfer) : TradeEvent
}