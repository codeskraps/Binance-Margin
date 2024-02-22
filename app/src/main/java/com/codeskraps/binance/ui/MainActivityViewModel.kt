package com.codeskraps.binance.ui

import com.codeskraps.binance.ui.mvi.MainActivityAction
import com.codeskraps.binance.ui.mvi.MainActivityEvent
import com.codeskraps.binance.ui.mvi.MainActivityState
import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.domain.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val binanceStore: BinanceStore
) : StateReducerViewModel<MainActivityState, MainActivityEvent, MainActivityAction>(
    initialState = MainActivityState.Initial
) {

    init {
        state.handleEvent(MainActivityEvent.CheckApiKey)
    }

    override fun reduceState(
        currentState: MainActivityState,
        event: MainActivityEvent
    ): MainActivityState {
        return when (event) {
            is MainActivityEvent.HasApiKey -> onHasApiKey(
                currentState = currentState,
                hasApiKey = event.hasApiKey
            )

            is MainActivityEvent.CheckApiKey -> onCheckApiKey(
                currentState = currentState
            )
        }
    }

    private fun onHasApiKey(
        currentState: MainActivityState,
        hasApiKey: Boolean
    ): MainActivityState {
        return currentState.copy(
            hasApiKey = hasApiKey
        )
    }

    private fun onCheckApiKey(
        currentState: MainActivityState
    ): MainActivityState {
        if (binanceStore.apiKey.isNotBlank()) {
            state.handleEvent(MainActivityEvent.HasApiKey(true))
        } else {
            state.handleEvent(MainActivityEvent.HasApiKey(false))
        }
        return currentState
    }
}