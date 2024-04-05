package com.codeskraps.feature.symbol.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.codeskraps.core.domain.R
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlin.math.roundToInt

@Composable
fun StochRSIChart(
    stochRsi: List<Pair<Float, Float>>
) {
    val stochK = stochRsi.map { it.first }
    val stochD = stochRsi.map { it.second }

    val entriesK = stochK.mapIndexed { index, value -> entryOf(index.toFloat(), value) }
    val entriesD = stochD.mapIndexed { index, value -> entryOf(index.toFloat(), value) }
    val twenty = List(stochK.size) { 20f }.mapIndexed { index, fl -> entryOf(index.toFloat(), fl) }
    val eighty = List(stochK.size) { 80f }.mapIndexed { index, fl -> entryOf(index.toFloat(), fl) }

    val chartEntryModel = entryModelOf(twenty, eighty, entriesK, entriesD)

    val verticalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Vertical.End> { value, _ ->
            value.roundToInt().toString()
        }
    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { _, _ ->
            ""
        }

    val axisOverride = AxisValuesOverrider.fixed(
        maxX = stochK.size.toFloat(),
        maxY = 100f,
        minY = 0f,
        minX = 0f
    )

    Chart(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        chart = lineChart(
            spacing = 2.dp,
            axisValuesOverrider = axisOverride,
            lines = listOf(
                LineChart.LineSpec(
                    lineThicknessDp = .5f,
                    lineColor = Color.WHITE
                ),
                LineChart.LineSpec(
                    lineThicknessDp = .5f,
                    lineColor = Color.WHITE
                ),
                LineChart.LineSpec(
                    lineThicknessDp = 1f,
                    lineColor = colorResource(id = R.color.margin_level_red).toArgb()
                ),
                LineChart.LineSpec(
                    lineThicknessDp = 1f,
                    lineColor = Color.argb(255, 0, 136, 255)
                )
            ),
        ),
        model = chartEntryModel,
        autoScaleUp = AutoScaleUp.Full,
        endAxis = rememberEndAxis(valueFormatter = verticalAxisValueFormatter),
        bottomAxis = rememberBottomAxis(valueFormatter = horizontalAxisValueFormatter)
    )
}