package com.codeskraps.feature.account.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.PnL
import com.codeskraps.core.domain.model.PnLTimeType
import com.codeskraps.feature.account.mvi.AccountEvent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun PnLChart(
    pnl: List<PnL>,
    currentPnl: Double,
    pnlTime: PnLTimeType,
    handleEvent: (AccountEvent) -> Unit
) {

    val entries = listOf(*pnl.map { it.pnl.toFloat() }.toTypedArray(), currentPnl.toFloat())
    val chartEntryModel = entryModelOf(*entries.toTypedArray())
    val lineColor = colorResource(id = R.color.margin_level_red.takeIf {
        pnl.first().pnl > currentPnl
    } ?: R.color.margin_level_green)

    val verticalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
            value.roundToInt().toString()
        }
    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { _, _ ->
            ""
        }

    val axisOverride = AxisValuesOverrider.fixed(
        maxX = entries.size.toFloat() - 1,
        maxY = (entries.max() * 1.1f).toInt().toFloat(),
        minY = (entries.min() - (entries.min().absoluteValue * 0.1)).toInt().toFloat(),
        minX = .0f
    )

    val decorations = listOf(
        ThresholdLine(
            thresholdValue = .0f,
            lineComponent = ShapeComponent(
                color = colorResource(id = R.color.margin_level_amber).toArgb(),
                strokeWidthDp = 0.5f
            )
        )
    ).takeIf { entries.max() > 0 && entries.min() < 0 } ?: emptyList()

    Chart(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        chart = lineChart(
            spacing = 4.dp,
            axisValuesOverrider = axisOverride,
            decorations = decorations,
            lines = listOf(
                LineChart.LineSpec(
                    lineThicknessDp = 0.5f,
                    lineColor = lineColor.toArgb(),
                    lineBackgroundShader = DynamicShaders.fromBrush(
                        Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.5f),
                                lineColor.copy(alpha = 0.1f)
                            )
                        )
                    )
                )
            ),
        ),
        model = chartEntryModel,
        autoScaleUp = AutoScaleUp.Full,
        startAxis = rememberStartAxis(valueFormatter = verticalAxisValueFormatter),
        bottomAxis = rememberBottomAxis(valueFormatter = horizontalAxisValueFormatter)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        PnLTimeType.entries.forEach {
            PnLTimeButton(currentPnLTimeType = pnlTime, pnlTime = it, handleEvent = handleEvent)
        }
    }
}

@Composable
fun PnLTimeButton(
    currentPnLTimeType: PnLTimeType,
    pnlTime: PnLTimeType,
    handleEvent: (AccountEvent) -> Unit
) {
    val selected = currentPnLTimeType == pnlTime
    OutlinedButton(
        onClick = { handleEvent(AccountEvent.PnLTimeChanged(pnlTime)) },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black.takeIf { selected } ?: Color.DarkGray
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = pnlTime.value,
            color = colorResource(id = R.color.margin_level_amber.takeIf { selected }
                ?: R.color.white)
        )
    }
}
