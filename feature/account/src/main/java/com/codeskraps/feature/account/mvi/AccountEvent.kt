package com.codeskraps.feature.account.mvi

import com.codeskraps.core.domain.model.MarginAccount
import com.codeskraps.core.domain.model.PnL
import com.codeskraps.feature.account.model.Entry
import com.codeskraps.core.domain.model.PnLTimeType

sealed interface AccountEvent {
    data object Resume : AccountEvent
    data object Pause : AccountEvent
    data class AccountLoaded(
        val account: MarginAccount,
        val ticker: List<com.codeskraps.core.domain.model.Ticker>,
        val invested: Double,
        val entries: List<Entry>
    ) : AccountEvent

    data object Ticker : AccountEvent
    data class TickerLoaded(val ticker: List<com.codeskraps.core.domain.model.Ticker>) :
        AccountEvent

    data class PnLLoaded(val pnl: List<PnL>) : AccountEvent
    data class PnLTimeChanged(val time: PnLTimeType) : AccountEvent
}