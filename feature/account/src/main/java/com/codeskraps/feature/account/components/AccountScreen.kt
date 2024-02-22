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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.codeskraps.core.domain.model.AssertSort
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier,
    state: AccountState,
    handleEvent: (AccountEvent) -> Unit,
    navRoute: (String) -> Unit
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
                    IconButton(onClick = { navRoute("setting") }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                    }
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
                        val selectedIndex = state.assetsSort.ordinal
                        val selectSort = AssertSort.entries.map { it.value }

                        LazyColumn {
                            item {
                                if (state.pnlEntries.size > 1) {
                                    PnLChart(
                                        entries = state.pnlEntries,
                                        pnlTime = state.pnlTime,
                                        handleEvent = handleEvent
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                }

                                Text(text = "Base:")
                                Spacer(modifier = Modifier.height(10.dp))
                                CardAsset(
                                    state = state,
                                    asset = acc.userAssets.first { it.asset == Constants.BASE_ASSET })
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "Assets(${
                                        acc.userAssets
                                            .filter { it.asset != Constants.BASE_ASSET }
                                            .filter { it.netAsset != .0 }.size
                                    }):"
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    LargeDropdownMenu(
                                        items = selectSort,
                                        selectedIndex = selectedIndex,
                                        onItemSelected = { index, _ ->
                                            handleEvent(AccountEvent.AssetsSortLoaded(AssertSort.entries[index]))
                                        },
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            items(acc.userAssets
                                .filter { it.asset != Constants.BASE_ASSET }
                                .filter { it.netAsset != .0 }
                                .sortedBy {
                                    when (selectedIndex) {
                                        0 -> state.value(it)
                                        1 -> state.investedAsset(it)
                                        2 -> state.pnLAsset(it)
                                        3 -> state.pnlAssetPercent(it)
                                        else -> state.value(it)
                                    }
                                }
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