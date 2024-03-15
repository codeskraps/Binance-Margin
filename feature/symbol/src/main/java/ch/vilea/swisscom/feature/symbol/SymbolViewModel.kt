package ch.vilea.swisscom.feature.symbol

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.vilea.swisscom.feature.symbol.mvi.SymbolAction
import ch.vilea.swisscom.feature.symbol.mvi.SymbolEvent
import ch.vilea.swisscom.feature.symbol.mvi.SymbolState
import com.codeskraps.core.domain.model.Candle
import com.codeskraps.core.domain.model.Interval
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.github.mikephil.charting.data.CandleEntry
import dagger.hilt.android.lifecycle.ActivityRetainedSavedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SymbolViewModel @Inject constructor(
    private val useCases: SymbolUseCases,
    private val savedState: SavedStateHandle
) : StateReducerViewModel<SymbolState, SymbolEvent, SymbolAction>(SymbolState.initialState) {

    private var candlesNetworkLoading: Boolean = true
    private var ordersNetworkLoading: Boolean = true
    override fun reduceState(currentState: SymbolState, event: SymbolEvent): SymbolState {
        return when (event) {
            is SymbolEvent.Resume -> onResume(currentState)
            is SymbolEvent.Pause -> onPause(currentState)
            is SymbolEvent.LoadedCandles -> onCandlesLoaded(currentState, event.candles)
            is SymbolEvent.LoadedOrders -> onOrdersLoaded(currentState, event.orders)
            is SymbolEvent.ChartTimeChanged -> onChartTimeChanged(currentState, event.interval)
            is SymbolEvent.StopLoading -> onStopLoading(currentState)
        }
    }

    private fun onResume(currentState: SymbolState): SymbolState {
        val symbol = savedState.get<String>("symbol") ?: ""
        val entry = savedState.get<String>("entry")?.toDouble() ?: .0
        val interval = useCases.getChartTimeUseCase()

        viewModelScope.launch(Dispatchers.IO) {
            val candles = useCases.getCandlesUseCase(symbol, interval, 100).map()
            state.handleEvent(SymbolEvent.LoadedCandles(candles))
        }

        viewModelScope.launch(Dispatchers.IO) {
            useCases.getOrdersUseCase(symbol) {
                ordersNetworkLoading = !it
                checkLoading()
            }.collect {
                state.handleEvent(SymbolEvent.LoadedOrders(it))
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
        return currentState.copy(orders = orders)
    }

    private fun onChartTimeChanged(
        currentState: SymbolState,
        interval: Interval
    ): SymbolState {
        candlesNetworkLoading = true
        useCases.putChartTimeUseCase(interval)

        val symbol = currentState.symbol
        viewModelScope.launch(Dispatchers.IO) {
            val candles = useCases.getCandlesUseCase(symbol, interval, 100).map()
            state.handleEvent(SymbolEvent.LoadedCandles(candles))
        }

        return currentState.copy(
            isLoading = true,
            interval = interval
        )
    }

    private fun onStopLoading(currentState: SymbolState): SymbolState {
        return currentState.copy(isLoading = false)
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
}