package com.codeskraps.feature.trades

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val useCases: TradeUseCases
) : StateReducerViewModel<TradesState, TradeEvent, TradeAction>(TradesState.initial) {

    private var allTrades = emptyList<Trade>()
    private var allOrders = emptyList<Order>()
    private var tradesNetworkLoading: Boolean = true
    private var transfersNetworkLoading: Boolean = true

    private var tradesJob: Job? = null
    private var ordersJob: Job? = null
    private var transfersJob: Job? = null

    override fun reduceState(currentState: TradesState, event: TradeEvent): TradesState {
        return when (event) {
            is TradeEvent.Resume -> onResume(currentState)
            is TradeEvent.Pause -> onPause(currentState)
            is TradeEvent.LoadedTrades -> onLoadedTrades(currentState, event.trades)
            is TradeEvent.LoadedOrders -> onLoadedOrders(currentState, event.orders)
            is TradeEvent.LoadedTransfers -> onLoadedTransfers(currentState, event.transfers)
            is TradeEvent.TradesSelection -> onTradesSelection(currentState, event.index)
            is TradeEvent.OrderSelection -> onOrderSelection(currentState, event.index)
            is TradeEvent.StopLoading -> onStopLoading(currentState)
            is TradeEvent.PriceUpdate -> onPriceUpdate(currentState, event.transfer)
            is TradeEvent.DeleteOrder -> onDeleteOrder(currentState, event.order)
        }
    }

    private fun onResume(currentState: TradesState): TradesState {
        tradesJob = viewModelScope.launch(Dispatchers.IO) {
            useCases.getTrades {
                tradesNetworkLoading = !it
                checkLoading()
            }.collect { trades ->
                state.handleEvent(TradeEvent.LoadedTrades(trades))
            }
        }
        ordersJob = viewModelScope.launch(Dispatchers.IO) {
            useCases.getOrders().collect { orders ->
                state.handleEvent(TradeEvent.LoadedOrders(orders))
            }
        }
        transfersJob = viewModelScope.launch(Dispatchers.IO) {
            useCases.getTransfers {
                transfersNetworkLoading = !it
                checkLoading()
            }.collect { transfers ->
                state.handleEvent(TradeEvent.LoadedTransfers(transfers))
            }
        }
        return currentState.copy(isLoading = true)
    }

    private fun onPause(currentState: TradesState): TradesState {
        tradesJob?.cancel()
        ordersJob?.cancel()
        transfersJob?.cancel()
        return currentState
    }

    private fun onLoadedTrades(currentState: TradesState, trades: List<Trade>): TradesState {
        val symbols = mutableSetOf("All Trades")
        symbols.addAll(trades.map { it.symbol }.sorted())
        allTrades = trades
        val symbol = symbols.elementAt(currentState.tradeSelection)
        return currentState.copy(
            trades = allTrades.takeIf { currentState.tradeSelection == 0 }
                ?: allTrades.filter { it.symbol == symbol },
            tradeSymbols = symbols.toSet(),
        )
    }

    private fun onLoadedOrders(currentState: TradesState, orders: List<Order>): TradesState {
        val symbols = mutableSetOf("All Orders")
        symbols.addAll(orders.map { it.symbol }.sorted())
        allOrders = orders
        val symbol = symbols.elementAt(currentState.ordersSelection)
        return currentState.copy(
            orders = allOrders.takeIf { currentState.ordersSelection == 0 }
                ?: allOrders.filter { it.symbol == symbol },
            ordersSymbols = symbols.toSet(),
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

    private fun onTradesSelection(currentState: TradesState, index: Int): TradesState {
        val symbol = currentState.tradeSymbols.elementAt(index)
        return currentState.copy(
            trades = allTrades.takeIf { index == 0 }
                ?: allTrades.filter { it.symbol == symbol },
            tradeSelection = index
        )
    }

    private fun onOrderSelection(currentState: TradesState, index: Int): TradesState {
        val symbol = currentState.ordersSymbols.elementAt(index)
        return currentState.copy(
            orders = allOrders.takeIf { index == 0 }
                ?: allOrders.filter { it.symbol == symbol },
            ordersSelection = index
        )
    }

    private fun onPriceUpdate(currentState: TradesState, transfer: Transfer): TradesState {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.updatePrice(transfer)
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

    private fun onDeleteOrder(currentState: TradesState, order: Order): TradesState {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.deleteOrder(order)
        }
        return currentState
    }

    private fun checkLoading() {
        if (!tradesNetworkLoading && !transfersNetworkLoading) {
            state.handleEvent(TradeEvent.StopLoading)
        }
    }
}