package com.codeskraps.feature.account.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.util.StateUtil
import com.codeskraps.feature.account.mvi.AccountState

@Composable
fun HeaderItem(
    state: AccountState,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Column {
            Text(text = "Total Balance")
            Text(
                text = "$ ${StateUtil.formatCurrency(state.totalAssetOfUSDT)}",
                fontSize = 28.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${state.pnlPercent.format(2)}%",
                textAlign = TextAlign.End,
                fontSize = 18.sp,
                color = state.pnlColor(pnl = state.pnlPercent)
            )
            Text(
                text = "PnL: $ ${StateUtil.formatCurrency(state.pnl)}",
                textAlign = TextAlign.End,
                fontSize = 18.sp,
                color = state.pnlColor(pnl = state.pnlPercent)
            )
        }
    }
    Spacer(modifier = Modifier.width(5.dp))
    Spacer(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(Color.White)
    )
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)