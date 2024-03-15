package ch.vilea.swisscom.feature.symbol.mvi

import com.codeskraps.core.domain.model.Interval
import com.codeskraps.core.domain.model.Order
import com.github.mikephil.charting.data.CandleEntry

data class SymbolState(
    val isLoading: Boolean,
    val symbol: String,
    val entries: List<CandleEntry>,
    val interval: Interval,
    val entry: Double,
    val orders: List<Order>
) {
    companion object {
        val initialState = SymbolState(
            isLoading = false,
            symbol = "",
            entries = emptyList(),
            interval = Interval.DAILY,
            entry = .0,
            orders = emptyList()
        )
    }
}
