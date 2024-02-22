package com.codeskraps.feature.trades.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.R
import com.codeskraps.core.client.model.Order
import com.codeskraps.core.client.model.Trade
import com.codeskraps.feature.trades.mvi.TradeEvent
import com.codeskraps.feature.trades.mvi.TradesState
import com.codeskraps.core.domain.util.StateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeScreen(
    modifier: Modifier,
    state: TradesState,
    handleEvent: (TradeEvent) -> Unit
) {
    LifecycleResumeEffect(Unit) {
        handleEvent(TradeEvent.Resume)
        onPauseOrDispose {}
    }

    Scaffold(
        modifier = modifier,
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
                })
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

            var tabIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf(
                "Trades(${state.trades.size})",
                "Orders(${state.orders.size})"
            )

            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }

            when (tabIndex) {
                0 -> TradesScreen(state)
                1 -> OrdersScreen(state)
            }
        }
    }
}

@Composable
private fun TradesScreen(state: TradesState) {
    if (state.trades.isNotEmpty()) {
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

@Composable
private fun OrdersScreen(state: TradesState) {
    if (state.orders.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            items(state.orders) { order ->
                OrderCard(order = order)
            }
        }
    }
}

@Composable
private fun TradeCard(trade: Trade) {
    Card(modifier = Modifier.padding(bottom = 10.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                if (StateUtil.logo(trade.symbol) != 0) {
                    Image(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(bottom = 5.dp),
                        painter = painterResource(id = StateUtil.logo(trade.symbol)),
                        contentDescription = "logo"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = trade.symbol,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = trade.displayTime)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(colorResource(id = if (trade.isBuyer) R.color.margin_level_green else R.color.margin_level_red))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("price: ${trade.price}")
                Spacer(modifier = Modifier.weight(1f))
                Text("value: $${trade.quoteQty.format(2)}")
            }
            Text("qty: $${trade.qty}")
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    Card(modifier = Modifier.padding(bottom = 10.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                if (StateUtil.logo(order.symbol) != 0) {
                    Image(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(bottom = 5.dp),
                        painter = painterResource(id = StateUtil.logo(order.symbol)),
                        contentDescription = "logo"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = order.symbol,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = order.displayTime)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(colorResource(id = if (order.side == "BUY") R.color.margin_level_green else R.color.margin_level_red))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("type: ${order.type}")
                Spacer(modifier = Modifier.weight(1f))
                Text("value: $${(order.price.toDouble() * order.origQty.toDouble()).format(2)}")
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("price: $${order.price.format(2)}")
                Spacer(modifier = Modifier.weight(1f))
                Text("qty: ${order.origQty}")
            }
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)