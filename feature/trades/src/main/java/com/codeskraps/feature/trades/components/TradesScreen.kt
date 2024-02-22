package com.codeskraps.feature.trades.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            var selectedIndex by remember { mutableIntStateOf(0) }
            LargeDropdownMenu(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                label = "Select Symbol:",
                items = state.tradeSymbols.toList(),
                selectedIndex = selectedIndex,
                onItemSelected = { index, _ ->
                    selectedIndex = index
                    handleEvent(TradeEvent.TradesSelection(state.tradeSymbols.toList()[index]))
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