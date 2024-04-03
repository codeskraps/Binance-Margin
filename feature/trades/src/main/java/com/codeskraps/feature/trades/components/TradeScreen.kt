package com.codeskraps.feature.trades.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.components.BinanceScaffold
import com.codeskraps.feature.trades.components.order.OrdersScreen
import com.codeskraps.feature.trades.components.trade.TradesScreen
import com.codeskraps.feature.trades.components.transfer.TransfersScreen
import com.codeskraps.feature.trades.mvi.TradeEvent
import com.codeskraps.feature.trades.mvi.TradesState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TradeScreen(
    modifier: Modifier,
    state: TradesState,
    handleEvent: (TradeEvent) -> Unit
) {
    LifecycleResumeEffect(Unit) {
        handleEvent(TradeEvent.Resume)
        onPauseOrDispose {
            handleEvent(TradeEvent.Pause)
        }
    }

    val scope = rememberCoroutineScope()

    BinanceScaffold(
        modifier = modifier,
        actions = {
            if (state.isLoading) CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    ) {
        val tabs = listOf(
            "Trades(${state.trades.size})",
            "Orders(${state.orders.size})",
            "Transfers(${state.transfers.size})"
        )
        val pagerState = rememberPagerState(pageCount = { tabs.size })
        val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

        TabRow(selectedTabIndex = selectedTabIndex.value) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = selectedTabIndex.value == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> TradesScreen(state, handleEvent)
                1 -> OrdersScreen(state, handleEvent)
                2 -> TransfersScreen(state, handleEvent)
            }
        }
    }
}