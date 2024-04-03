package com.codeskraps.core.domain.usecases.symbol

import android.util.Log
import com.codeskraps.core.domain.model.Candle
import javax.inject.Inject

class StochRSIUseCase @Inject constructor(
    private val rsiUseCase: RSIUseCase
) {
    operator fun invoke(
        candles: List<Candle>,
        periodRSI: Int = 14,
        periodStoch: Int = 14,
        smoothK: Int = 3,
        smoothD: Int = 3
    ): List<Pair<Float, Float>> {

        val rsi = rsiUseCase(candles, periodRSI)
        //Log.i("StochRSIUseCase", "RSI: $rsi")
        val stoch = rsi
            .windowed(periodStoch, 1) { (it.last() - it.min()) / (it.max() - it.min()) }
            .map { it * 100 }
        //Log.i("StochRSIUseCase", "STOCH: $stoch")

        val stochK = stoch.windowed(smoothK, 1) { it.average().toFloat() }
        val stochD = stochK.windowed(smoothD, 1) { it.average().toFloat() }

        return stochK.zip(stochD).map { (k, d) -> k to d }
    }
}