package com.codeskraps.feature.pnl.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.components.BinanceScaffold
import com.codeskraps.feature.pnl.mvi.PnLEvent
import com.codeskraps.feature.pnl.mvi.PnLState

@Composable
fun FinishTradesScreen(
    modifier: Modifier,
    state: PnLState,
    handleEvent: (PnLEvent) -> Unit
) {
    LifecycleResumeEffect(Unit) {
        handleEvent(PnLEvent.Resume)
        onPauseOrDispose {
            handleEvent(PnLEvent.Pause)
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
        if (state.finishTrades.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                HeaderItem(state = state)
                LazyColumn {
                    item {
                        PnLChart(entries = state.pnlEntries)
                        LargeDropdownMenu(
                            label = "Select Symbol:",
                            items = state.finishTradeSymbols.toList(),
                            selectedIndex = state.finishTradeSelection,
                            onItemSelected = { index, _ ->
                                handleEvent(PnLEvent.FinishSelection(index))
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    items(state.finishTrades) { finishTrade ->
                        FinishTradeCard(finishTrade = finishTrade)
                    }
                }
            }
        }
    }
}