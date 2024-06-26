package com.codeskraps.feature.symbol.mvi

import com.codeskraps.feature.symbol.SuperGuppy
import com.codeskraps.core.domain.model.Interval
import com.codeskraps.core.domain.model.Order
import com.github.mikephil.charting.data.CandleEntry

data class SymbolState(
    val isLoading: Boolean,
    val symbol: String,
    val entries: List<CandleEntry>,
    val interval: Interval,
    val entry: Double,
    val orders: List<Order>,
    val superGuppy: SuperGuppy?,
    val visibility: Boolean,
    val rsi: List<Float>,
    val stochRSI: List<Pair<Float, Float>>
) {
    companion object {
        val initialState = SymbolState(
            isLoading = false,
            symbol = "",
            entries = emptyList(),
            interval = Interval.DAILY,
            entry = .0,
            orders = emptyList(),
            superGuppy = null,
            visibility = true,
            rsi = emptyList(),
            stochRSI = emptyList()
        )
    }
}
