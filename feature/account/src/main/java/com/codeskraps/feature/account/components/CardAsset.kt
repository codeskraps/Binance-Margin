package com.codeskraps.feature.account.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.codeskraps.core.domain.model.Asset
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.core.domain.util.StateUtil
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState

@Composable
fun CardAsset(
    state: AccountState,
    asset: Asset,
    handleEvent: (AccountEvent) -> Unit
) {

    val modifier = if (asset.asset != Constants.BASE_ASSET) {
        Modifier
            .padding(bottom = 10.dp)
            .clickable {
                handleEvent(
                    AccountEvent.OpenSymbol(
                        symbol = "${asset.asset}${Constants.BASE_ASSET}",
                        entry = state.entry(asset)
                    )
                )
            }
    } else {
        Modifier.padding(bottom = 10.dp)
    }

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Image(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(bottom = 5.dp),
                    painter = painterResource(id = StateUtil.logo(asset)),
                    contentDescription = "logo"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${asset.asset} $${StateUtil.formatCurrency(state.value(asset))}",
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
                    Text(text = "Invested: $${StateUtil.formatCurrency(state.investedAsset(asset))}")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "${state.valueAssetPercent(asset).format(2)}%")
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    val decimal = StateUtil.decimal(asset)
                    Text(text = "price $${state.price(asset).format(decimal)}")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "entry $${state.entry(asset).format(decimal)}")
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                if (asset.asset == Constants.BASE_ASSET) {
                    Text(text = "free: $${StateUtil.formatCurrency(asset.free)}")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "net: $${StateUtil.formatCurrency(asset.netAsset)}")
                } else {
                    Text(text = "free: ${asset.free}")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "net: ${asset.netAsset}")
                }
            }
            if (asset.borrowed != .0 || asset.interest != .0) {
                if (asset.asset == Constants.BASE_ASSET) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "debt: $${StateUtil.formatCurrency(asset.borrowed)}")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "interest: $${StateUtil.formatCurrency(asset.interest)}")
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
                        Text(text = "locked: $${StateUtil.formatCurrency(asset.locked)}")
                        Spacer(modifier = Modifier.weight(1f))
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "locked: ${asset.locked}")
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (state.orders(asset).isNotEmpty()) {
                Text(text = "Orders:")
                state.orders(asset).forEach {
                    Text(
                        text = "  ${it.side}" +
                                " ${it.type}" +
                                " $${it.price.format(StateUtil.decimal(asset.asset))}" +
                                " ${it.origQty}" +
                                " $${(StateUtil.formatCurrency(it.price * it.origQty))}",
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)