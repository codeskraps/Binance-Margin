package com.codeskraps.feature.watchlist.mvi

import com.codeskraps.core.domain.model.WatchlistItem

sealed interface WatchlistEvent {
    data object Resume : WatchlistEvent
    data object Pause : WatchlistEvent
    data class WatchlistLoaded(val watchlist: List<WatchlistItem>) : WatchlistEvent
}