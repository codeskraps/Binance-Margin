package com.codeskraps.feature.pnl.components

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
import com.codeskraps.core.domain.model.FinishTrade
import com.codeskraps.core.domain.model.TradeType
import com.codeskraps.core.domain.util.StateUtil

@Composable
fun FinishTradeCard(finishTrade: FinishTrade) {
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
                if (StateUtil.logo(finishTrade.symbol) != 0) {
                    Image(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(bottom = 5.dp),
                        painter = painterResource(id = StateUtil.logo(finishTrade.symbol)),
                        contentDescription = "logo"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = finishTrade.symbol,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = finishTrade.displayTime)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(colorResource(id = if (finishTrade.type == TradeType.LONG) R.color.margin_level_green else R.color.margin_level_red))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("PnL: $${StateUtil.formatCurrency(finishTrade.pnl)}")
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${finishTrade.pnlPercent.format(2)}%",
                    color = colorResource(id = if (finishTrade.pnl > .0) R.color.margin_level_green else R.color.margin_level_red)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Entry: $${finishTrade.entryPrice.format(StateUtil.decimal(finishTrade.symbol))}")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Exit: $${finishTrade.exitPrice.format(StateUtil.decimal(finishTrade.symbol))}")
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Trades: ${finishTrade.trades}")
                Spacer(modifier = Modifier.weight(1f))
                Text("Time: ${finishTrade.tradeLength}")
            }
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)