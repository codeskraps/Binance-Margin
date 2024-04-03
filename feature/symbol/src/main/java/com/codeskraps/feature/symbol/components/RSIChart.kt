package com.codeskraps.feature.symbol.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun RSIChart(
    rsi: List<Float>
) {
    val chartEntryModel = entryModelOf(*rsi.toTypedArray())
    val verticalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Vertical.End> { value, _ ->
            value.roundToInt().toString()
        }
    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { _, _ ->
            ""
        }

    val axisOverride = AxisValuesOverrider.fixed(
        maxX = rsi.size.toFloat(),
        maxY = 100f,
        minY = 0f,
        minX = 0f
    )

    Chart(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        chart = lineChart(
            spacing = 4.dp,
            axisValuesOverrider = axisOverride,
            lines = listOf(
                LineChart.LineSpec(
                    lineThicknessDp = 0.5f,
                    lineColor = Color.YELLOW
                )
            ),
        ),
        model = chartEntryModel,
        autoScaleUp = AutoScaleUp.Full,
        endAxis = rememberEndAxis(valueFormatter = verticalAxisValueFormatter),
        bottomAxis = rememberBottomAxis(valueFormatter = horizontalAxisValueFormatter)
    )
}