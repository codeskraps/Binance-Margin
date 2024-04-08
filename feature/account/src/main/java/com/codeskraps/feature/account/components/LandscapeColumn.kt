package com.codeskraps.feature.account.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState

@Composable
fun LandscapeColumn(
    state: AccountState,
    handleEvent: (AccountEvent) -> Unit,
) {
    state.account.let { acc ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp)
        ) {
            item(span = {
                GridItemSpan(maxLineSpan)
            }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HeaderItem(state = state)
                    StatsItem(state = state)
                    ChartItem(state = state, handleEvent = handleEvent)
                }
            }

            if (state.account.userAssets.isNotEmpty()) {
                items(acc.userAssets
                    .filter { it.asset != Constants.BASE_ASSET }
                    .filter { it.netAsset != .0 }
                    .sortedBy {
                        when (state.assetsSort.ordinal) {
                            0 -> state.value(it)
                            1 -> state.investedAsset(it)
                            2 -> state.pnLAsset(it)
                            3 -> state.pnlAssetPercent(it)
                            else -> state.value(it)
                        }
                    }
                    .reversed()) { asset ->
                    Box(modifier = Modifier.padding(end = 10.dp)) {
                        CardAsset(state = state, asset = asset, handleEvent = handleEvent)
                    }
                }
            }
        }
    }
}