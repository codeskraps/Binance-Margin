package com.codeskraps.feature.symbol

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.codeskraps.feature.symbol.mvi.SymbolAction
import com.codeskraps.feature.symbol.mvi.SymbolEvent
import com.codeskraps.feature.symbol.mvi.SymbolState
import com.codeskraps.core.domain.model.Candle
import com.codeskraps.core.domain.model.EntryPrice
import com.codeskraps.core.domain.model.Interval
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SymbolViewModel @Inject constructor(
    private val useCases: SymbolUseCases,
    private val savedState: SavedStateHandle
) : StateReducerViewModel<SymbolState, SymbolEvent, SymbolAction>(SymbolState.initialState) {

    companion object {
        private const val CHART_LENGTH = 100
    }

    private var candlesNetworkLoading: Boolean = true
    private var ordersNetworkLoading: Boolean = true
    override fun reduceState(currentState: SymbolState, event: SymbolEvent): SymbolState {
        return when (event) {
            is SymbolEvent.Resume -> onResume(currentState)
            is SymbolEvent.Pause -> onPause(currentState)
            is SymbolEvent.LoadedCandles -> onCandlesLoaded(currentState, event.candles)
            is SymbolEvent.LoadedOrders -> onOrdersLoaded(currentState, event.orders)
            is SymbolEvent.LoadedEntryPrice -> onEntryPriceLoaded(currentState, event.entryPrice)
            is SymbolEvent.ChartTimeChanged -> onChartTimeChanged(currentState, event.interval)
            is SymbolEvent.SuperGuppyLoaded -> onSuperGuppyLoaded(currentState, event.superGuppy)
            is SymbolEvent.StopLoading -> onStopLoading(currentState)
            is SymbolEvent.VisibilityChanged -> onVisibilityChanged(currentState, event.visibility)
            is SymbolEvent.RSI -> onRSI(currentState, event.rsi)
            is SymbolEvent.StochRSI -> onStochRSI(currentState, event.stochRSI)
        }
    }

    private fun onResume(currentState: SymbolState): SymbolState {
        val symbol = savedState.get<String>("symbol") ?: ""
        val entry = savedState.get<String>("entry")?.toDouble() ?: .0
        val interval = useCases.getChartTime()

        candles(symbol, interval)

        viewModelScope.launch(Dispatchers.IO) {
            useCases.getOrders(symbol).collect {
                state.handleEvent(SymbolEvent.LoadedOrders(it))
            }
        }

        if (entry == .0) {
            viewModelScope.launch(Dispatchers.IO) {
                useCases.getRealmEntryPrice(symbol)?.let { entryPrice ->
                    if (entryPrice.price != .0)
                        state.handleEvent(SymbolEvent.LoadedEntryPrice(entryPrice))
                }
            }
        }

        return currentState.copy(
            isLoading = true,
            symbol = symbol,
            entry = entry,
            interval = interval
        )
    }

    private fun onPause(currentState: SymbolState): SymbolState {
        return currentState.copy(isLoading = false)
    }

    private fun onCandlesLoaded(
        currentState: SymbolState,
        candles: List<CandleEntry>
    ): SymbolState {
        candlesNetworkLoading = false
        checkLoading()

        return currentState.copy(entries = candles)
    }

    private fun onOrdersLoaded(
        currentState: SymbolState,
        orders: List<Order>
    ): SymbolState {
        ordersNetworkLoading = false
        checkLoading()

        return currentState.copy(orders = orders)
    }

    private fun onEntryPriceLoaded(
        currentState: SymbolState,
        entryPrice: EntryPrice
    ): SymbolState {
        return currentState.copy(entry = entryPrice.price)
    }

    private fun onChartTimeChanged(
        currentState: SymbolState,
        interval: Interval
    ): SymbolState {
        candlesNetworkLoading = true
        useCases.putChartTime(interval)

        val symbol = currentState.symbol
        candles(symbol, interval)

        return currentState.copy(
            isLoading = true,
            interval = interval
        )
    }

    private fun onSuperGuppyLoaded(
        currentState: SymbolState,
        superGuppy: SuperGuppy,
    ): SymbolState {
        return currentState.copy(superGuppy = superGuppy)
    }

    private fun onStopLoading(currentState: SymbolState): SymbolState {
        return currentState.copy(isLoading = false)
    }

    private fun onVisibilityChanged(currentState: SymbolState, visibility: Boolean): SymbolState {
        return currentState.copy(visibility = visibility)
    }

    private fun onRSI(currentState: SymbolState, rsi: List<Float>): SymbolState {
        return currentState.copy(rsi = rsi)
    }

    private fun onStochRSI(
        currentState: SymbolState,
        stochRSI: List<Pair<Float, Float>>
    ): SymbolState {
        return currentState.copy(stochRSI = stochRSI)
    }

    private fun candles(symbol: String, interval: Interval) {
        viewModelScope.launch(Dispatchers.IO) {
            val candles = useCases.getCandles(symbol, interval, 200)
            superGuppy(candles)
            //rsi(candles)
            stochasticRSI(candles)

            val candleEntries = candles.takeLast(CHART_LENGTH).map()

            state.handleEvent(SymbolEvent.LoadedCandles(candleEntries))
        }
    }

    private fun superGuppy(candles: List<Candle>) {
        viewModelScope.launch(Dispatchers.IO) {
            val superGuppy = useCases.superGuppy(candles)

            val fastEntries = superGuppy.fast.map(candles)
            val medEntries = superGuppy.med.map(candles)
            val slowEntries = superGuppy.slow.map(candles)

            state.handleEvent(
                SymbolEvent.SuperGuppyLoaded(
                    SuperGuppy(
                        fastEntries,
                        medEntries,
                        slowEntries,
                        colFinal = superGuppy.colFinal,
                        colFinal2 = superGuppy.colFinal2
                    )
                )
            )
        }
    }

    @Suppress("unused")
    private fun rsi(candles: List<Candle>) {
        viewModelScope.launch(Dispatchers.IO) {
            val rsi = useCases.rsi(candles)
            state.handleEvent(SymbolEvent.RSI(rsi.takeLast(CHART_LENGTH)))
        }
    }

    private fun stochasticRSI(candles: List<Candle>) {
        viewModelScope.launch(Dispatchers.IO) {
            val stochRSI = useCases.stochRSI(candles)
            state.handleEvent(SymbolEvent.StochRSI(stochRSI.takeLast(CHART_LENGTH)))
        }
    }

    private fun checkLoading() {
        if (!candlesNetworkLoading && !ordersNetworkLoading) {
            state.handleEvent(SymbolEvent.StopLoading)
        }
    }

    private fun List<Candle>.map(): List<CandleEntry> {
        return map {
            CandleEntry(it.closeTime, it.high, it.low, it.open, it.close)
        }
    }

    private fun List<Float>.map(candles: List<Candle>): List<Entry> {
        return takeLast(CHART_LENGTH)
            .zip(candles.takeLast(CHART_LENGTH))
            .map { (value, candleEntry) ->
                Entry(candleEntry.closeTime, value)
            }
    }
}