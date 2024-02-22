package com.codeskraps.feature.trades

import androidx.lifecycle.viewModelScope
import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.codeskraps.feature.trades.mvi.TradeAction
import com.codeskraps.feature.trades.mvi.TradeEvent
import com.codeskraps.feature.trades.mvi.TradesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val client: BinanceClient,
    private val store: BinanceStore
) : StateReducerViewModel<TradesState, TradeEvent, TradeAction>() {

    override fun initState(): TradesState = TradesState.initial

    override fun reduceState(currentState: TradesState, event: TradeEvent): TradesState {
        return when (event) {
            is TradeEvent.Resume -> onResume(currentState)
            is TradeEvent.Loaded -> onLoaded(currentState, event)
        }
    }

    private fun onResume(currentState: TradesState): TradesState {
        viewModelScope.launch(Dispatchers.IO) {
            val symbols = store.tradedAssets.map { "$it${Constants.BASE_ASSET}" }
            val deferredTrades = async { client.trades(symbols) }
            val deferredOrders = async { client.orders(symbols) }
            val trades = deferredTrades.await()
            val orders = deferredOrders.await()
            state.handleEvent(TradeEvent.Loaded(trades, orders))
        }
        return currentState.copy(isLoading = true)
    }

    private fun onLoaded(currentState: TradesState, event: TradeEvent.Loaded): TradesState {
        return currentState.copy(
            isLoading = false,
            trades = event.trades,
            orders = event.orders
        )
    }
}