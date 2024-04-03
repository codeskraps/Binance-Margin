package com.codeskraps.feature.watchlist.mvi

import com.codeskraps.core.domain.model.WatchlistItem

data class WatchlistState(
    val isLoading: Boolean = false,
    val watchlist: List<WatchlistItem> = emptyList()
) {
    companion object {
        val initial = WatchlistState()
    }
}
