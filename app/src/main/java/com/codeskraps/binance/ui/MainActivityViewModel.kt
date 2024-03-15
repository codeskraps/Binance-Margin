package com.codeskraps.binance.ui

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.codeskraps.binance.ui.mvi.MainActivityAction
import com.codeskraps.binance.ui.mvi.MainActivityEvent
import com.codeskraps.binance.ui.mvi.MainActivityState
import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.domain.pnl.PnLWorker
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.codeskraps.core.domain.workers.OrdersWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val application: Application,
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
            is MainActivityEvent.Resume -> onResume(currentState)
            is MainActivityEvent.HasApiKey -> onHasApiKey(
                currentState = currentState,
                hasApiKey = event.hasApiKey
            )

            is MainActivityEvent.CheckApiKey -> onCheckApiKey(
                currentState = currentState
            )
        }
    }

    private fun onResume(currentState: MainActivityState): MainActivityState {
        WorkManager.getInstance(application).run {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Log.i(TAG, "onReceive1")
            enqueue(
                OneTimeWorkRequestBuilder<OrdersWorker>()
                    .setConstraints(constraints)
                    .build()
            )

            //Log.i(TAG, "onReceive: ${operation.result} || ${operation.state}")
        }

        return currentState
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