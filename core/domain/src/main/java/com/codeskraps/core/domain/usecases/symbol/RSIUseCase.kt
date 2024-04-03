package com.codeskraps.core.domain.usecases.symbol

import com.codeskraps.core.domain.model.Candle
import javax.inject.Inject
import kotlin.math.abs


class RSIUseCase @Inject constructor(

) {

    operator fun invoke(candles: List<Candle>, period: Int = 14): List<Float> {

        val gain = candles.mapIndexed { index, candle ->
            if (index == 0) {
                .0f
            } else {
                val diff = candle.close - candles[index - 1].close
                if (diff > 0) diff else .0f
            }
        }
        val loss = candles.mapIndexed { index, candle ->
            if (index == 0) {
                .0f
            } else {
                val diff = candle.close - candles[index - 1].close
                if (diff < 0) abs(diff) else .0f
            }
        }

        val avgGain = gain.windowed(period, 1) { it.average().toFloat() }
        val avgLoss = loss.windowed(period, 1) { it.average().toFloat() }

        val rsi = avgGain.zip(avgLoss).map { (avgGain, avgLoss) ->
            100 - (100 / (1 + avgGain / avgLoss))
        }

        return rsi
    }
}