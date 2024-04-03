package com.codeskraps.core.domain.usecases.watchlist

import android.util.Log
import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.client.model.CandleDto
import com.codeskraps.core.domain.mappers.toTicker
import com.codeskraps.core.domain.model.Interval
import com.codeskraps.core.domain.model.Ticker
import com.codeskraps.core.domain.model.WatchlistItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetWatchlistUseCase @Inject constructor(
    private val client: BinanceClient,
    private val store: BinanceStore
) {
    suspend operator fun invoke(): List<WatchlistItem> {
        return coroutineScope {
            Log.i("GetWatchlistUseCase", "${client.tradedSymbols()}")
            val badSymbols = store.badSymbols
            val tradedSymbols =
                client.tradedSymbols().filter {
                    !badSymbols.contains(it) &&
                            !it.startsWith("USDC")
                            && !it.startsWith("USDT")
                            && !it.startsWith("FDUSD")
                }

            val deferredResults = listOf(
                *tradedSymbols.map { symbol ->
                    async {
                        client.kLines(
                            symbol = symbol,
                            interval = Interval.HOURLY.value,
                            limit = 24
                        )
                    }
                }.toTypedArray()
            )

            val results = awaitAll(*deferredResults.toTypedArray())

            return@coroutineScope (results as List<*>).filterIsInstance(List::class.java)
                .mapIndexed { index, candles ->
                    val newPrice = (candles.last() as CandleDto).close
                    val oldPrice = (candles.first() as CandleDto).open

                    WatchlistItem(
                        symbol = tradedSymbols[index],
                        price = newPrice,
                        priceChange = newPrice - oldPrice,
                        priceChangePercent = ((newPrice - oldPrice) / oldPrice) * 100
                    )
                }
                .sortedBy { it.price }
                .reversed()
        }
    }
}