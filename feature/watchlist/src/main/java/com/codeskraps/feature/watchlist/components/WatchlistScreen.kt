package com.codeskraps.feature.watchlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.components.BinanceScaffold
import com.codeskraps.feature.watchlist.mvi.WatchlistEvent
import com.codeskraps.feature.watchlist.mvi.WatchlistState

@Composable
fun WatchlistScreen(
    modifier: Modifier,
    state: WatchlistState,
    handleEvent: (WatchlistEvent) -> Unit,
    navRoute: (String) -> Unit
) {
    LifecycleResumeEffect(Unit) {
        handleEvent(WatchlistEvent.Resume)
        onPauseOrDispose {
            handleEvent(WatchlistEvent.Pause)
        }
    }

    BinanceScaffold(
        modifier = modifier,
        title = { Text(text = "Binance Margin") },
        actions = {
            if (state.isLoading) CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            itemsIndexed(state.watchlist) { index, item ->
                WatchlistItem(
                    item = item,
                    navRoute = navRoute
                )
                if (index < state.watchlist.size - 1) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.DarkGray)
                    )
                }
            }
        }
    }
}