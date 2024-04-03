package com.codeskraps.feature.watchlist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.WatchlistItem
import com.codeskraps.core.domain.navigation.Screen
import com.codeskraps.core.domain.util.StateUtil

@Composable
fun WatchlistItem(
    item: WatchlistItem,
    navRoute: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { navRoute(Screen.Symbol.createRoute(item.symbol, .0)) }
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(id = StateUtil.logo(item.symbol)),
                contentDescription = "logo"
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = item.symbol, fontSize = 22.sp)
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = StateUtil.formatCurrency(item.price, StateUtil.decimal(item.symbol)),
                        fontSize = 18.sp
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = "${if (item.priceChange > 0) "+" else ""}${item.priceChange.format(2)}",
                        color = colorResource(id = if (item.priceChange > 0) R.color.margin_level_green else R.color.margin_level_red)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${if (item.priceChangePercent > 0) "+" else ""}${
                            item.priceChangePercent.format(
                                2
                            )
                        }%",
                        color = colorResource(id = if (item.priceChangePercent > 0) R.color.margin_level_green else R.color.margin_level_red)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)