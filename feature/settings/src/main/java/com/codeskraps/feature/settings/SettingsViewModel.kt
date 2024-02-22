package com.codeskraps.feature.settings

import androidx.lifecycle.viewModelScope
import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.codeskraps.feature.settings.mvi.SettingsAction
import com.codeskraps.feature.settings.mvi.SettingsEvent
import com.codeskraps.feature.settings.mvi.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val store: BinanceStore
) : StateReducerViewModel<SettingsState, SettingsEvent, SettingsAction>(SettingsState.initial) {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            state.handleEvent(SettingsEvent.UpdateApiKey(store.apiKey))
            state.handleEvent(SettingsEvent.UpdateSecretKey(store.secretKey))
            state.handleEvent(SettingsEvent.UpdateDate(store.startDate))
        }
    }

    override fun reduceState(currentState: SettingsState, event: SettingsEvent): SettingsState {
        return when (event) {
            is SettingsEvent.UpdateApiKey -> onUpdateApiKey(currentState, event.apiKey)
            is SettingsEvent.UpdateSecretKey -> onUpdateSecretKey(currentState, event.secretKey)
            is SettingsEvent.UpdateDate -> onUpdateDate(currentState, event.date)
        }
    }

    private fun onUpdateApiKey(currentState: SettingsState, apiKey: String): SettingsState {
        viewModelScope.launch(Dispatchers.IO) {
            store.apiKey = apiKey
        }
        return currentState.copy(apiKey = apiKey)
    }

    private fun onUpdateSecretKey(currentState: SettingsState, secretKey: String): SettingsState {
        viewModelScope.launch(Dispatchers.IO) {
            store.secretKey = secretKey
        }
        return currentState.copy(secretKey = secretKey)
    }

    private fun onUpdateDate(currentState: SettingsState, date: Long): SettingsState {
        viewModelScope.launch(Dispatchers.IO) {
            store.startDate = date
        }
        return currentState.copy(startDate = date)
    }
}