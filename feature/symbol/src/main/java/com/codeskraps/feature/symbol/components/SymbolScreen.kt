package com.codeskraps.feature.symbol.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.components.BinanceScaffold
import com.codeskraps.core.domain.model.Interval
import com.codeskraps.core.domain.util.StateUtil
import com.codeskraps.feature.symbol.mvi.SymbolEvent
import com.codeskraps.feature.symbol.mvi.SymbolState

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

    BinanceScaffold(actions = {
        if (state.isLoading) CircularProgressIndicator(
            modifier = Modifier
                .size(50.dp)
                .padding(10.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp)
        ) {
            Text(text = state.symbol, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(10.dp))
            val price = state.entries.lastOrNull()?.close ?: 0.0
            Row(modifier = Modifier.fillMaxWidth()) {
                val entry =
                    if (state.entry != .0) "entry: $${state.entry.format(StateUtil.decimal(state.symbol))}"
                    else ""
                Text(text = entry)
                Spacer(modifier = Modifier.weight(1f))
                Text("ticker: ${price.toDouble().format(StateUtil.decimal(state.symbol))}")
            }

            LazyColumn {
                item {
                    if (state.entries.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            SymbolChart(
                                entries = state.entries,
                                superGuppy = state.superGuppy,
                                entry = state.entry,
                                orders = state.orders,
                                visibility = state.visibility
                            )

                            val icon = if (state.visibility)
                                R.drawable.ic_visibility
                            else R.drawable.ic_visibility_off

                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp),
                                onClick = {
                                    handleEvent(SymbolEvent.VisibilityChanged(!state.visibility))
                                }) {
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = null
                                )
                            }
                        }
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

                    if (state.rsi.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        RSIChart(rsi = state.rsi)
                    }

                    if (state.stochRSI.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        StochRSIChart(stochRsi = state.stochRSI)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (state.orders.isNotEmpty()) {
                    items(state.orders) { order ->
                        OrderCard(order = order)
                    }
                } else {
                    item {
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
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)