package com.codeskraps.feature.watchlist

import com.codeskraps.core.domain.usecases.watchlist.GetWatchlistUseCase
import javax.inject.Inject

class WatchlistUseCases @Inject constructor(
    val getWatchlist: GetWatchlistUseCase
)
