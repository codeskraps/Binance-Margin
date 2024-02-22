package com.codeskraps.feature.trades

import androidx.lifecycle.viewModelScope
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.codeskraps.feature.trades.mvi.TradeAction
import com.codeskraps.feature.trades.mvi.TradeEvent
import com.codeskraps.feature.trades.mvi.TradesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val usesCases: TradeUsesCases
) : StateReducerViewModel<TradesState, TradeEvent, TradeAction>(TradesState.initial) {

    private var allTrades = emptyList<Trade>()
    private var tradesNetworkLoading: Boolean = true
    private var ordersNetworkLoading: Boolean = true

    override fun reduceState(currentState: TradesState, event: TradeEvent): TradesState {
        return when (event) {
            is TradeEvent.Resume -> onResume(currentState)
            is TradeEvent.LoadedTrades -> onLoadedTrades(currentState, event.trades)
            is TradeEvent.LoadedOrders -> onLoadedOrders(currentState, event.orders)
            is TradeEvent.TradesSelection -> onTradesSelection(currentState, event.symbol)
            is TradeEvent.StopLoading -> onStopLoading(currentState)
        }
    }

    private fun onResume(currentState: TradesState): TradesState {
        val symbols = usesCases.getTradedSymbols()
        viewModelScope.launch(Dispatchers.IO) {
            usesCases.getTrades(symbols) {
                tradesNetworkLoading = !it
                checkLoading()
            }.collect {
                state.handleEvent(TradeEvent.LoadedTrades(it))
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            usesCases.getOrders(symbols) {
                ordersNetworkLoading = !it
                checkLoading()
            }.collect {
                state.handleEvent(TradeEvent.LoadedOrders(it))
            }
        }
        return currentState.copy(isLoading = true)
    }

    private fun onLoadedTrades(currentState: TradesState, trades: List<Trade>): TradesState {
        val symbols = mutableSetOf("All Trades")
        symbols.addAll(trades.map { it.symbol }.sorted())
        allTrades = trades
        return currentState.copy(
            trades = allTrades.takeIf { currentState.tradeSelection == "All Trades" }
                ?: allTrades.filter { it.symbol == currentState.tradeSelection },
            tradeSymbols = symbols.toSet(),
        )
    }

    private fun onLoadedOrders(currentState: TradesState, orders: List<Order>): TradesState {
        return currentState.copy(
            orders = orders
        )
    }

    private fun onTradesSelection(currentState: TradesState, symbol: String): TradesState {
        return currentState.copy(
            trades = allTrades.takeIf { symbol == "All Trades" }
                ?: allTrades.filter { it.symbol == symbol },
            tradeSelection = symbol
        )
    }

    private fun onStopLoading(currentState: TradesState): TradesState {
        return currentState.copy(isLoading = false)
    }

    private fun checkLoading() {
        if (!tradesNetworkLoading && !ordersNetworkLoading) {
            state.handleEvent(TradeEvent.StopLoading)
        }
    }
}