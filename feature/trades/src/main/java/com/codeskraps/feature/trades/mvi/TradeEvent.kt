package com.codeskraps.feature.trades.mvi

import com.codeskraps.core.client.model.Order
import com.codeskraps.core.client.model.Trade

sealed interface TradeEvent {
    data object Resume : TradeEvent
    data class Loaded(val trades: List<com.codeskraps.core.client.model.Trade>, val orders: List<com.codeskraps.core.client.model.Order>) :
        TradeEvent
}