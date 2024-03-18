package com.codeskraps.feature.account.mvi

import com.codeskraps.core.domain.model.AssertSort
import com.codeskraps.core.domain.model.MarginAccount
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.model.PnLTimeType
import com.codeskraps.core.domain.model.Ticker
import com.codeskraps.feature.account.model.Entry

sealed interface AccountEvent {
    data object Resume : AccountEvent
    data object Pause : AccountEvent
    data class AccountLoaded(
        val account: MarginAccount,
        val btcPrice: Double,
        val invested: Double,
        val entries: List<Entry>,
        val orders: List<Order>,
        val maxBorrow: Double
    ) : AccountEvent

    data object LoadTicker : AccountEvent
    data class TickerLoaded(val ticker: List<Ticker>) : AccountEvent

    data class PnLLoaded(val pnl: List<Float>) : AccountEvent
    data class PnLTimeChanged(val time: PnLTimeType) : AccountEvent
    data class AssetsSortLoaded(val assetsSort: AssertSort) : AccountEvent
    data class OpenSymbol(val symbol: String, val entry: Double) : AccountEvent
}