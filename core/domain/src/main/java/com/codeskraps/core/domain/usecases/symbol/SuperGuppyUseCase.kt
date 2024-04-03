package com.codeskraps.core.domain.usecases.symbol

import android.graphics.Color
import android.util.Log
import com.codeskraps.core.domain.model.Candle
import javax.inject.Inject

data class SuperGuppy(
    val fast: List<Float>,
    val med: List<Float>,
    val slow: List<Float>,
    val colFinal: Int,
    val colFinal2: Int
)

class SuperGuppyUseCase @Inject constructor(

) {
    companion object {
        val TAG: String = SuperGuppyUseCase::class.java.simpleName
        val fast = listOf(3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23)
        val slow = listOf(25, 28, 31, 34, 37, 40, 43, 46, 49, 52, 55, 58, 61, 64, 67, 70)
    }

    operator fun invoke(candles: List<Candle>): SuperGuppy {
        val fastEma = fast.map { candles.ema(it) }
        val slowEma = slow.map { candles.ema(it) }

        /*fastEma.forEachIndexed { index, list ->
            Log.i(TAG, "fastEma($index): $list")
        }
        slowEma.forEachIndexed { index, list ->
            Log.i(TAG, "slowEma($index): $list")
        }*/

        /*
        var fastSums = fastEma.first()
        fastEma.subList(1, fastEma.size).forEach { list ->
            fastSums = fastSums.mapIndexed { i, sum -> sum + list[i] }
        }
        fastSums.map { it / fast.size }

        var slowSums = slowEma.first()
        slowEma.subList(1, slowEma.size).forEach { list ->
            slowSums = slowSums.mapIndexed { i, sum -> sum + list[i] }
        }
        slowSums.map { it / slow.size }

        val ema200 = candles.ema(200)*/

        val fastEmaLast = fastEma.map { it.last() }
        val slowEmaLast = slowEma.map { it.last() }

        /*Log.i(TAG, "fastEmaLast: $fastEmaLast")
        Log.i(TAG, "slowEmaLast: $slowEmaLast")*/

        val colFastL = fastEmaLast.asSequence().windowed(2).all { (a, b) -> a >= b }
        val colFastS = fastEmaLast.asSequence().windowed(2).all { (a, b) -> a <= b }

        val colSlowL = slowEmaLast.asSequence().windowed(2).all { (a, b) -> a >= b }
        val colSlowS = slowEmaLast.asSequence().windowed(2).all { (a, b) -> a <= b }

        /*Log.i(
            TAG,
            "colFastL: $colFastL, " +
                    "colFastS: $colFastS, " +
                    "colSlowL: $colSlowL, " +
                    "colSlowS: $colSlowS"
        )*/

        val colFinal = if (colFastL && slowEma.first().last() > slowEma.last().last()) {
            Color.CYAN
        } else if (colFastS && slowEma.first().last() < slowEma.last().last()) {
            Color.CYAN
        } else {
            Color.GRAY
        }

        val colFinal2 = if (colSlowL) {
            Color.GREEN
        } else if (colSlowS) {
            Color.RED
        } else {
            Color.GRAY
        }

        return SuperGuppy(
            fast = fastEma.first(),
            med = slowEma.first(),
            slow = slowEma.last(),
            colFinal = colFinal,
            colFinal2 = colFinal2
        )
    }

    inner class EMA(length: Int) {
        private var ema = 0.0f
        private var multiplier = 2.0f / (length + 1)

        fun ema(close: Float): Float {
            ema = if (ema == 0.0f) {
                close
            } else {
                close * multiplier + ema * (1 - multiplier)
            }
            return ema
        }
    }

    private fun List<Candle>.ema(length: Int): List<Float> {
        val ema = EMA(length)
        return map { ema.ema(it.close) }
    }
}