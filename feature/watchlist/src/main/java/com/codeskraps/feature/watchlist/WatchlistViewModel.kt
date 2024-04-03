package com.codeskraps.feature.watchlist

import androidx.lifecycle.viewModelScope
import com.codeskraps.core.domain.model.WatchlistItem
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.codeskraps.feature.watchlist.mvi.WatchlistAction
import com.codeskraps.feature.watchlist.mvi.WatchlistEvent
import com.codeskraps.feature.watchlist.mvi.WatchlistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val useCases: WatchlistUseCases
) : StateReducerViewModel<WatchlistState, WatchlistEvent, WatchlistAction>(WatchlistState.initial) {

    private var watchlistJob: Job? = null
    private var isResume = false

    override fun reduceState(currentState: WatchlistState, event: WatchlistEvent): WatchlistState {
        return when (event) {
            is WatchlistEvent.Resume -> onResume(currentState)
            is WatchlistEvent.Pause -> onPause(currentState)
            is WatchlistEvent.WatchlistLoaded -> onWatchlistLoaded(currentState, event.watchlist)
        }
    }

    private fun onResume(currentState: WatchlistState): WatchlistState {
        isResume = true
        watchlistJob = viewModelScope.launch(Dispatchers.IO) {
            while (isResume) {
                val watchlist = useCases.getWatchlist()
                state.handleEvent(WatchlistEvent.WatchlistLoaded(watchlist))

                delay(1000)
            }
        }
        return currentState.copy(isLoading = true)
    }

    private fun onPause(currentState: WatchlistState): WatchlistState {
        watchlistJob?.cancel()
        isResume = false
        return currentState.copy(isLoading = false)
    }

    private fun onWatchlistLoaded(
        currentState: WatchlistState,
        watchlist: List<WatchlistItem>
    ): WatchlistState {
        return currentState.copy(watchlist = watchlist, isLoading = false)
    }
}