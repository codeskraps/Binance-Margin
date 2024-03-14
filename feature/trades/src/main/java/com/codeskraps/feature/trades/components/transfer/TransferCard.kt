package com.codeskraps.feature.trades.components.transfer

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.Transfer
import com.codeskraps.core.domain.util.StateUtil
import com.codeskraps.feature.trades.mvi.TradeEvent

@Composable
fun TransferCard(
    transfer: Transfer,
    handleEvent: (TradeEvent) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .padding(bottom = 10.dp)
        .clickable { showDialog = true }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                if (StateUtil.logo("${transfer.asset}USDT") != 0) {
                    Image(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(bottom = 5.dp),
                        painter = painterResource(id = StateUtil.logo("${transfer.asset}USDT")),
                        contentDescription = "logo"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = transfer.asset,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = transfer.displayTime())
            }
            Spacer(modifier = Modifier.height(5.dp))
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(colorResource(id = if (transfer.type == "ROLL_IN") R.color.margin_level_green else R.color.margin_level_red))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("type: ${transfer.type}")
                Spacer(modifier = Modifier.weight(1f))
                Text("value: $${(transfer.price * transfer.amount).format(2)}")
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                val price = if (transfer.price == 0.0) "Enter Price" else "$${
                    transfer.price.format(StateUtil.decimal(transfer.asset))
                }"
                Text("price: $price")
                Spacer(modifier = Modifier.weight(1f))
                Text("qty: ${transfer.amount}")
            }
        }
    }

    if (showDialog) {
        PriceEditDialog(
            value = transfer.price.toString(),
            dialogTitle = "Update Price for the Transfer of ${transfer.amount} in ${transfer.asset}",
            onSave = { newValue ->
                handleEvent(TradeEvent.PriceUpdate(transfer.copy(price = newValue.toDouble())))
            }) {
            showDialog = false
        }
    }
}

@Composable
private fun PriceEditDialog(
    value: String,
    dialogTitle: String,
    onSave: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var preferenceValue by remember { mutableStateOf(value) }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                onSave(preferenceValue)
            }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = dialogTitle) },
        text = {
            Column {
                OutlinedTextField(
                    value = preferenceValue,
                    onValueChange = { preferenceValue = it })
            }
        }
    )
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)