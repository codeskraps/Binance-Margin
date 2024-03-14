package com.codeskraps.feature.trades.components.trade

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codeskraps.feature.trades.components.LargeDropdownMenu
import com.codeskraps.feature.trades.mvi.TradeEvent
import com.codeskraps.feature.trades.mvi.TradesState

@Composable
fun TradesScreen(
    state: TradesState,
    handleEvent: (TradeEvent) -> Unit
) {
    if (state.trades.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(10.dp))

            LargeDropdownMenu(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                label = "Select Symbol:",
                items = state.tradeSymbols.toList(),
                selectedIndex = state.tradeSelection,
                onItemSelected = { index, _ ->
                    handleEvent(TradeEvent.TradesSelection(index))
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            ) {
                items(state.trades) { trade ->
                    TradeCard(trade = trade)
                }
            }
        }
    }
}