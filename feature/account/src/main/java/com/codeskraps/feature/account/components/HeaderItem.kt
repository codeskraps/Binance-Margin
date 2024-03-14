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
import com.codeskraps.feature.account.mvi.AccountState

@Composable
fun HeaderItem(
    state: AccountState,
) {
    state.account.let { acc ->
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Column {
                Text(text = "Total Balance")
                Text(text = "$ ${state.totalAssetOfUSDT.format(2)}", fontSize = 28.sp)
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
                    text = "PnL: $ ${state.pnl.format(2)}",
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
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Invested: $${state.invested.format(2)}")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Margin lvl: ${acc.marginLevel.format(2)}",
                color = state.marginLevelColor()
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Net: $${state.totalNetAssetOfUSDT.format(2)}")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Debt: $${state.totalLiabilityOfUSDT.format(2)}")
        }
        if (state.account.totalCollateralValueInUSDT != .0 || state.account.collateralMarginLevel != .0) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Collateral: $${
                        state.account.totalCollateralValueInUSDT.format(2)
                    }"
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Col. level: ${state.account.collateralMarginLevel.format(2)}")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Entry: $${state.entryValueAssets.format(2)}")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Assets: $${state.valueAssets.format(2)}")
        }
        Enabled(status = state.account.borrowEnabled, message = "Borrow Enabled: ")
        Enabled(status = state.account.tradeEnabled, message = "Trade Enabled: ")
        Enabled(status = state.account.transferEnabled, message = "Transfer Enabled: ")
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun Enabled(status: Boolean, message: String) {
    if (!status) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = message)
            Text(
                text = "FALSE",
                color = colorResource(id = R.color.margin_level_red)
            )
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)