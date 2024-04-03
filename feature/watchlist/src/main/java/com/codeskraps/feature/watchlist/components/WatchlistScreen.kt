package com.codeskraps.feature.watchlist.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.R
import com.codeskraps.feature.watchlist.mvi.WatchlistEvent
import com.codeskraps.feature.watchlist.mvi.WatchlistState

@OptIn(ExperimentalMaterial3Api::class)
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
}