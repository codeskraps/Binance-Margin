package com.codeskraps.feature.trades.components.trade

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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.domain.util.StateUtil

@Composable
fun TradeCard(trade: Trade) {
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
                Text(text = trade.displayTime())
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
                Text("price: ${trade.price.format(StateUtil.decimal(trade.symbol))}")
                Spacer(modifier = Modifier.weight(1f))
                Text("value: $${(trade.qty * trade.price).format(2)}")
            }
            Text("qty: ${trade.qty}")
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)