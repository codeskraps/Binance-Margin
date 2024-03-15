package ch.vilea.swisscom.feature.symbol.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import ch.vilea.swisscom.feature.symbol.mvi.SymbolEvent
import ch.vilea.swisscom.feature.symbol.mvi.SymbolState
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.Interval
import com.codeskraps.core.domain.util.StateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolScreen(
    state: SymbolState,
    handleEvent: (SymbolEvent) -> Unit
) {
    LifecycleResumeEffect(Unit) {
        handleEvent(SymbolEvent.Resume)
        onPauseOrDispose {
            handleEvent(SymbolEvent.Pause)
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

    val scaffoldModifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                Text(text = state.symbol)

                if (state.entries.isNotEmpty()) {
                    SymbolChart(
                        entries = state.entries,
                        entry = state.entry,
                        orders = state.orders
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Interval.entries.forEach {
                            ChartTimeButton(
                                selected = state.interval == it,
                                interval = it,
                                handleEvent = handleEvent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                val price = state.entries.lastOrNull()?.close ?: 0.0
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("entry: $${state.entry.format(StateUtil.decimal(state.symbol))}")
                    Spacer(modifier = Modifier.weight(1f))
                    Text("ticker: ${price.toDouble().format(StateUtil.decimal(state.symbol))}")
                }

                Spacer(modifier = Modifier.height(10.dp))
                if (state.orders.isNotEmpty()) {
                    LazyColumn {
                        items(state.orders) { order ->
                            OrderCard(order = order)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No orders found")
                    }
                }
            }
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)