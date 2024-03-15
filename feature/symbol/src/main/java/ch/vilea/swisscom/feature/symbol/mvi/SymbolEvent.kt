package ch.vilea.swisscom.feature.symbol.mvi

import com.codeskraps.core.domain.model.Interval
import com.codeskraps.core.domain.model.Order
import com.github.mikephil.charting.data.CandleEntry

sealed interface SymbolEvent {
    data object Resume : SymbolEvent
    data object Pause : SymbolEvent
    data class LoadedCandles(val candles: List<CandleEntry>) : SymbolEvent
    data class LoadedOrders(val orders: List<Order>) : SymbolEvent
    data class ChartTimeChanged(val interval: Interval) : SymbolEvent
    data object StopLoading : SymbolEvent
}