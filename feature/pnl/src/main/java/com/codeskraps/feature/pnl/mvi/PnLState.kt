package com.codeskraps.feature.pnl.mvi

import com.codeskraps.core.domain.model.FinishTrade

data class PnLState(
    val isLoading: Boolean,
    val totalPnL: Double,
    val totalTrades: Int,
    val winingTrades: Int,
    val longTrades: Int,
    val winingLongTrades: Int,
    val shortTrades: Int,
    val winingShortTrades: Int,
    val totalProfit: Double,
    val totalLoss: Double,
    val profitFactor: Double,
    val averagePnL: Double,
    val pnlEntries: List<Float>,
    val finishTrades: List<FinishTrade>,
    val finishTradeSymbols: Set<String>,
    val finishTradeSelection: Int
) {
    companion object {
        val initial = PnLState(
            isLoading = false,
            totalPnL = .0,
            totalTrades = 0,
            winingTrades = 0,
            longTrades = 0,
            winingLongTrades = 0,
            shortTrades = 0,
            winingShortTrades = 0,
            totalProfit = .0,
            totalLoss = .0,
            profitFactor = .0,
            averagePnL = .0,
            pnlEntries = emptyList(),
            finishTrades = emptyList(),
            finishTradeSymbols = emptySet(),
            finishTradeSelection = 0
        )
    }
}
