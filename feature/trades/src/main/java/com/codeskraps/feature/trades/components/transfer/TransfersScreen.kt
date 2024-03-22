package com.codeskraps.feature.trades.components.transfer

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
import com.codeskraps.feature.trades.mvi.TradeEvent
import com.codeskraps.feature.trades.mvi.TradesState

@Composable
fun TransfersScreen(
    state: TradesState,
    handleEvent: (TradeEvent) -> Unit
) {
    if (state.transfers.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp)
        ) {
            LazyColumn {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                items(state.transfers) { transfer ->
                    TransferCard(
                        transfer = transfer,
                        handleEvent = handleEvent
                    )
                }
            }
        }
    }
}