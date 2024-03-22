package com.codeskraps.feature.symbol.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codeskraps.core.domain.usecases.symbol.SuperGuppy
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
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun SuperGuppyChart(
    superGuppy: SuperGuppy,
    chartEntryModel: ChartEntryModel?
) {
    val verticalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Vertical.End> { value, _ ->
            value.roundToInt().toString()
        }
    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { _, _ ->
            ""
        }

    val entries = superGuppy.fast + superGuppy.med + superGuppy.slow
    val axisOverride = AxisValuesOverrider.fixed(
        maxX = superGuppy.fast.size.toFloat(),
        maxY = entries.max() + (entries.max().absoluteValue * 0.01f),
        minY = entries.min() - (entries.min().absoluteValue * 0.01f),
        minX = .0f
    )

    chartEntryModel?.let {
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
                        lineColor = superGuppy.colFinal
                    ),
                    LineChart.LineSpec(
                        lineThicknessDp = 0.5f,
                        lineColor = superGuppy.colFinal2
                    ),
                    LineChart.LineSpec(
                        lineThicknessDp = 0.5f,
                        lineColor = superGuppy.colFinal2
                    )
                ),
            ),
            model = it,
            autoScaleUp = AutoScaleUp.Full,
            endAxis = rememberEndAxis(valueFormatter = verticalAxisValueFormatter),
            bottomAxis = rememberBottomAxis(valueFormatter = horizontalAxisValueFormatter)
        )
    }
}
