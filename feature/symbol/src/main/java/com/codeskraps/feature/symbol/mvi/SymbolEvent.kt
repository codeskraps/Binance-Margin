package com.codeskraps.feature.symbol.mvi

import com.codeskraps.core.domain.model.EntryPrice
import com.codeskraps.feature.symbol.SuperGuppy
import com.codeskraps.core.domain.model.Interval
import com.codeskraps.core.domain.model.Order
import com.github.mikephil.charting.data.CandleEntry

sealed interface SymbolEvent {
    data object Resume : SymbolEvent
    data object Pause : SymbolEvent
    data class LoadedCandles(val candles: List<CandleEntry>) : SymbolEvent
    data class LoadedOrders(val orders: List<Order>) : SymbolEvent
    data class LoadedEntryPrice(val entryPrice: EntryPrice) : SymbolEvent
    data class ChartTimeChanged(val interval: Interval) : SymbolEvent
    data class SuperGuppyLoaded(val superGuppy: SuperGuppy) : SymbolEvent
    data object StopLoading : SymbolEvent
    data class VisibilityChanged(val visibility: Boolean) : SymbolEvent
    data class RSI(val rsi: List<Float>) : SymbolEvent
    data class StochRSI(val stochRSI: List<Pair<Float, Float>>) : SymbolEvent
}