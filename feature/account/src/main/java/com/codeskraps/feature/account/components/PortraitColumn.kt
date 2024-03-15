package com.codeskraps.feature.account.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState

@Composable
fun PortraitColumn(
    state: AccountState,
    handleEvent: (AccountEvent) -> Unit,
) {
    state.account.let { acc ->

        HeaderItem(state = state)

        if (state.account.userAssets.isNotEmpty()) {
            val selectedIndex = state.assetsSort.ordinal

            LazyColumn {
                item {
                    ChartItem(state = state, handleEvent = handleEvent)
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
                    CardAsset(state = state, asset = asset, handleEvent = handleEvent)
                }
            }
        }
    }
}