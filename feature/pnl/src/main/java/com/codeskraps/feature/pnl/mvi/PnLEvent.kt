package com.codeskraps.feature.pnl.mvi

import com.codeskraps.core.domain.model.FinishTrade

sealed interface PnLEvent {
    data object Resume : PnLEvent
    data object Pause : PnLEvent
    data class LoadedFinishTrade(val finishTrades: List<FinishTrade>) : PnLEvent
    data class FinishSelection(val index: Int) : PnLEvent
}