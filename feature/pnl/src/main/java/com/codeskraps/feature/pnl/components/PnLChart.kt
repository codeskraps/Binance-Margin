package com.codeskraps.feature.pnl.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.codeskraps.core.domain.R
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
    entries: List<Float>
) {

    val chartEntryModel = entryModelOf(*entries.toTypedArray())
    val lineColor = colorResource(id = R.color.margin_level_red.takeIf {
        entries.last() < 0
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
        maxY = entries.max() + (entries.max().absoluteValue * 0.01f),
        minY = entries.min() - (entries.min().absoluteValue * 0.01f),
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
}
