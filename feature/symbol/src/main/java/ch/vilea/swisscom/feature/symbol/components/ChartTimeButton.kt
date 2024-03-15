package ch.vilea.swisscom.feature.symbol.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import ch.vilea.swisscom.feature.symbol.mvi.SymbolEvent
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.Interval

@Composable
fun ChartTimeButton(
    interval: Interval,
    selected: Boolean,
    handleEvent: (SymbolEvent) -> Unit
) {
    val color = if (isSystemInDarkTheme()) {
        Color.Black.takeIf { selected } ?: Color.DarkGray
    } else {
        Color.White.takeIf { selected } ?: Color.LightGray
    }

    OutlinedButton(
        onClick = { handleEvent(SymbolEvent.ChartTimeChanged(interval)) },
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = interval.value,
            color = colorResource(id = R.color.margin_level_amber.takeIf { selected }
                ?: R.color.white)
        )
    }
}