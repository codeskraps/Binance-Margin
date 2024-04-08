package com.codeskraps.feature.account.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codeskraps.core.domain.model.AssertSort
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState

@Composable
fun ChartItem(
    state: AccountState,
    handleEvent: (AccountEvent) -> Unit,
) {
    state.account.let { acc ->
        val selectSort = AssertSort.entries.map { it.value }

        if (state.pnlEntries.size > 1) {
            PnLChart(
                entries = state.pnlEntries,
                pnlTime = state.pnlTime,
                handleEvent = handleEvent
            )
            Spacer(modifier = Modifier.height(5.dp))
        }

        /*
        Text(text = "Base:")
        Spacer(modifier = Modifier.height(10.dp))
        CardAsset(
            state = state,
            asset = acc.userAssets.first { it.asset == Constants.BASE_ASSET },
            handleEvent = handleEvent
        )*/
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
                selectedIndex = state.assetsSort.ordinal,
                onItemSelected = { index, _ ->
                    handleEvent(AccountEvent.AssetsSortLoaded(AssertSort.entries[index]))
                },
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}