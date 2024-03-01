package com.codeskraps.feature.trades

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.model.Transfer
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
    private var transfersNetworkLoading: Boolean = true

    override fun reduceState(currentState: TradesState, event: TradeEvent): TradesState {
        return when (event) {
            is TradeEvent.Resume -> onResume(currentState)
            is TradeEvent.LoadedTrades -> onLoadedTrades(currentState, event.trades)
            is TradeEvent.LoadedOrders -> onLoadedOrders(currentState, event.orders)
            is TradeEvent.LoadedTransfers -> onLoadedTransfers(currentState, event.transfers)
            is TradeEvent.TradesSelection -> onTradesSelection(currentState, event.symbol)
            is TradeEvent.StopLoading -> onStopLoading(currentState)
            is TradeEvent.PriceUpdate -> onPriceUpdate(currentState, event.transfer)
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
        viewModelScope.launch(Dispatchers.IO) {
            usesCases.getTransfers {
                transfersNetworkLoading = !it
                checkLoading()
            }.collect {
                it.forEach {
                    Log.e("Transfer", it.toString())
                }
                state.handleEvent(TradeEvent.LoadedTransfers(it))
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

    private fun onLoadedTransfers(
        currentState: TradesState,
        transfers: List<Transfer>
    ): TradesState {
        return currentState.copy(
            transfers = transfers
        )
    }

    private fun onTradesSelection(currentState: TradesState, symbol: String): TradesState {
        return currentState.copy(
            trades = allTrades.takeIf { symbol == "All Trades" }
                ?: allTrades.filter { it.symbol == symbol },
            tradeSelection = symbol
        )
    }

    private fun onPriceUpdate(currentState: TradesState, transfer: Transfer): TradesState {
        viewModelScope.launch(Dispatchers.IO) {
            usesCases.updatePriceUseCase(transfer)
        }
        return currentState.copy(
            transfers = currentState.transfers.map {
                if (it.txId == transfer.txId) {
                    transfer
                } else {
                    it
                }
            }
        )
    }

    private fun onStopLoading(currentState: TradesState): TradesState {
        return currentState.copy(isLoading = false)
    }

    private fun checkLoading() {
        if (!tradesNetworkLoading && !ordersNetworkLoading && !transfersNetworkLoading) {
            state.handleEvent(TradeEvent.StopLoading)
        }
    }
}