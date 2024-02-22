package com.codeskraps.feature.account.components

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.core.domain.util.StateUtil
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier,
    state: AccountState,
    handleEvent: (AccountEvent) -> Unit
) {
    LifecycleResumeEffect(Unit) {
        handleEvent(AccountEvent.Resume)
        onPauseOrDispose {
            handleEvent(AccountEvent.Pause)
        }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                state.account.let { acc ->
                    Text(text = "Total Balance")
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                        Text(text = "$ ${state.totalAssetOfUSDT.format(2)}", fontSize = 28.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "PnL: $${
                                state.pnl.format(2)
                            }  ${state.pnlPercent.format(2)}%",
                            textAlign = TextAlign.End,
                            fontSize = 18.sp,
                            color = state.pnlColor(pnl = state.pnlPercent)
                        )
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
                    Text(text = "Assets: $${state.investedAssets.format(2)}")
                    Enabled(status = state.account.borrowEnabled, message = "Borrow Enabled: ")
                    Enabled(status = state.account.tradeEnabled, message = "Trade Enabled: ")
                    Enabled(status = state.account.transferEnabled, message = "Transfer Enabled: ")
                    Spacer(modifier = Modifier.height(10.dp))

                    if (acc.userAssets.isNotEmpty()) {
                        LazyColumn {
                            item {
                                if (state.pnlEntries.isNotEmpty()) {
                                    PnLChart(
                                        state.pnlEntries,
                                        state.pnl,
                                        state.pnlTime,
                                        handleEvent
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                }

                                Text(text = "Base:")
                                Spacer(modifier = Modifier.height(10.dp))
                                CardAsset(
                                    state = state,
                                    asset = acc.userAssets.first { it.asset == Constants.BASE_ASSET })
                                Text(text = "Assets(${
                                    acc.userAssets
                                        .filter { it.asset != Constants.BASE_ASSET }
                                        .filter { it.netAsset != .0 }.size
                                }):"
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            items(acc.userAssets
                                .filter { it.asset != Constants.BASE_ASSET }
                                .filter { it.netAsset != .0 }
                                .sortedBy { state.value(it) }
                                .reversed())
                            { asset ->
                                if (asset.asset != Constants.BASE_ASSET)
                                    CardAsset(state = state, asset = asset)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardAsset(state: AccountState, asset: com.codeskraps.core.domain.model.Asset) {
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
                if (StateUtil.logo(asset) != 0) {
                    Image(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(bottom = 5.dp),
                        painter = painterResource(id = StateUtil.logo(asset)),
                        contentDescription = "logo"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${asset.asset} $${state.value(asset).format(2)}",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                if (state.pnLAsset(asset) != .0) {
                    Text(
                        text = "$${
                            state.pnLAsset(asset).format(2)
                        }  ${state.pnlAssetPercent(asset).format(2)}%",
                        color = state.pnlColor(pnl = state.pnlAssetPercent(asset))
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(colorResource(id = if (asset.netAsset < 0) R.color.margin_level_red else R.color.margin_level_green))
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (state.investedAsset(asset) != .0) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Invested: $${state.investedAsset(asset).format(2)}")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "${state.investedAssetPercent(asset).format(2)}%")
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    val decimal = state.decimal(asset)
                    Text(text = "price $${state.price(asset).format(decimal)}")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "entry $${state.entry(asset).format(decimal)}")
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                if (asset.asset == Constants.BASE_ASSET) {
                    Text(text = "free: $${asset.free.format(2)}")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "net: $${asset.netAsset.format(2)}")
                } else {
                    Text(text = "free: ${asset.free}")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "net: ${asset.netAsset}")
                }
            }
            if (asset.borrowed != .0 || asset.interest != .0) {
                if (asset.asset == Constants.BASE_ASSET) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "debt: $${asset.borrowed.format(2)}")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "interest: $${asset.interest.format(2)}")
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "debt: ${asset.borrowed}")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "interest: ${asset.interest}")
                    }
                }
            }
            if (asset.locked != .0) {
                if (asset.asset == Constants.BASE_ASSET) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "locked: $${asset.locked.format(2)}")
                        Spacer(modifier = Modifier.weight(1f))
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "locked: ${asset.locked}")
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
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