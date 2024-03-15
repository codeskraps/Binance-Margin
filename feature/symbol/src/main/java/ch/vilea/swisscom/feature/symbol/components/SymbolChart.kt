package ch.vilea.swisscom.feature.symbol.components

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.Order
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.utils.Utils


@Composable
fun SymbolChart(
    entries: List<CandleEntry>,
    entry: Double,
    orders: List<Order>
) {
    Utils.init(LocalContext.current)
    val resources = LocalContext.current.resources
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
    val redColor = ResourcesCompat.getColor(resources, R.color.margin_level_red, null)
    val greenColor = ResourcesCompat.getColor(resources, R.color.margin_level_green, null)
    val amberColor = ResourcesCompat.getColor(resources, R.color.margin_level_amber, null)

    val entryLimit = LimitLine(entry.toFloat(), "Entry").apply {
        setLineWidth(2f)
        //ll1.enableDashedLine(10f, 10f, 0f)
        labelPosition = LimitLabelPosition.RIGHT_TOP
        lineColor = amberColor
        setTextSize(10f)
    }
    val orderLimits = orders.map { order ->
        LimitLine(order.price.toFloat(), order.side).apply {
            setLineWidth(2f)
            //ll1.enableDashedLine(10f, 10f, 0f)
            labelPosition = LimitLabelPosition.RIGHT_TOP
            lineColor = if (order.side == "BUY") greenColor else redColor
            setTextSize(10f)
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { context ->
            CandleStickChart(context).apply {
                setBackgroundColor(backgroundColor)
                isHighlightPerDragEnabled = false
                legend.isEnabled = false

                with(axisLeft) {
                    setDrawLabels(false)
                    removeAllLimitLines()
                    addLimitLine(entryLimit)
                    orderLimits.forEach { addLimitLine(it) }
                }

                with(axisRight) {
                    setDrawGridLines(false)
                    textColor = Color.WHITE
                }

                with(xAxis) {
                    setDrawGridLines(false)
                    setDrawLabels(false)
                    setGranularity(1f)
                    setAvoidFirstLastClipping(true)
                    isGranularityEnabled = true
                }
            }
        },
        update = { chart ->
            chart.data = CandleData(CandleDataSet(entries, "Candles").apply {
                setDrawIcons(false)
                setColor(Color.rgb(80, 80, 80))
                axisDependency = YAxis.AxisDependency.LEFT
                shadowColor = Color.GRAY
                shadowWidth = 0.7f
                decreasingColor = redColor
                decreasingPaintStyle = Paint.Style.FILL_AND_STROKE
                increasingColor = greenColor
                increasingPaintStyle = Paint.Style.FILL_AND_STROKE
                neutralColor = Color.BLUE
                highlightLineWidth = 2f
            })
            chart.invalidate()
        }
    )
}
