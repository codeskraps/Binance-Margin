package com.codeskraps.feature.pnl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeskraps.feature.pnl.mvi.PnLState

@Composable
fun HeaderItem(
    state: PnLState
) {
    Text(text = "PnL")
    Text(text = "$ ${state.totalPnL.format(2)}", fontSize = 28.sp)
    Spacer(modifier = Modifier.width(5.dp))
    Spacer(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(Color.White)
    )
    Spacer(modifier = Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Trades: ${state.winingTrades}/${state.totalTrades}")
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Long: ${state.winingLongTrades}/${state.longTrades}")
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "PnL: $${state.totalProfit.format(2)}/$${state.totalLoss.format(2)}")
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Short: ${state.winingShortTrades}/${state.shortTrades}")
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Profit Factor: ${state.profitFactor.format(2)}")
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Avg PnL: $${state.averagePnL.format(2)}")
    }
    Spacer(modifier = Modifier.height(10.dp))
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)