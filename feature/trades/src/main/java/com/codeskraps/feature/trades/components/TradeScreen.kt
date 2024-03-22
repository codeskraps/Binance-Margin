package com.codeskraps.feature.trades.components

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.R
import com.codeskraps.feature.trades.components.order.OrdersScreen
import com.codeskraps.feature.trades.components.trade.TradesScreen
import com.codeskraps.feature.trades.components.transfer.TransfersScreen
import com.codeskraps.feature.trades.mvi.TradeEvent
import com.codeskraps.feature.trades.mvi.TradesState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val configuration = LocalConfiguration.current

    val scrollBehavior = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )

        Configuration.ORIENTATION_PORTRAIT -> TopAppBarDefaults.enterAlwaysScrollBehavior(
            rememberTopAppBarState()
        )

        else -> TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    }

    val scaffoldModifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(5.dp),
                        painter = painterResource(id = R.mipmap.ic_launcher),
                        contentDescription = ""
                    )
                },
                title = { Text(text = "Binance Margin") },
                actions = {
                    if (state.isLoading) CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )

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
}