package com.codeskraps.feature.account.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.util.StateUtil
import com.codeskraps.feature.account.mvi.AccountState

@Composable
fun StatsItem(
    state: AccountState
) {
    state.account.let { acc ->
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Invested: $${StateUtil.formatCurrency(state.invested)}")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Margin lvl: ${acc.marginLevel.format(2)}",
                color = state.marginLevelColor()
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Net: $${StateUtil.formatCurrency(state.totalNetAssetOfUSDT)}")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${state.currentPnlPercent.format(2)}%",
                color = state.pnlColor(pnl = state.currentPnlPercent)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Debt: $${StateUtil.formatCurrency(state.totalLiabilityOfUSDT)}")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "uPnL $${StateUtil.formatCurrency(state.currentPnl, 2)}",
                color = state.pnlColor(pnl = state.currentPnlPercent)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Entry: $${StateUtil.formatCurrency(state.entryValueAssets)}")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Assets: $${StateUtil.formatCurrency(state.valueAssets)}")
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Free: $${StateUtil.formatCurrency(state.free)}")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Locked: $${StateUtil.formatCurrency(state.locked)}")
        }
        if (state.account.totalCollateralValueInUSDT != .0 || state.account.collateralMarginLevel != .0) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Collateral: $${
                        StateUtil.formatCurrency(state.account.totalCollateralValueInUSDT)
                    }"
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Col. level: ${StateUtil.formatCurrency(state.account.collateralMarginLevel)}")
            }
        }
        Text(text = "Max Borrow: $${StateUtil.formatCurrency(state.maxBorrow)}")
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