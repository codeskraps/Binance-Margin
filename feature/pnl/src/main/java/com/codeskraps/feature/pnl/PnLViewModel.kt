package com.codeskraps.feature.pnl

import androidx.lifecycle.viewModelScope
import com.codeskraps.core.domain.model.FinishTrade
import com.codeskraps.core.domain.model.TradeType
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.codeskraps.feature.pnl.mvi.PnLAction
import com.codeskraps.feature.pnl.mvi.PnLEvent
import com.codeskraps.feature.pnl.mvi.PnLState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class PnLViewModel @Inject constructor(
    private val useCases: PnlUseCases
) : StateReducerViewModel<PnLState, PnLEvent, PnLAction>(PnLState.initial) {

    private var allFinishTrades = emptyList<FinishTrade>()
    private var finishTradesJob: Job? = null
    override fun reduceState(currentState: PnLState, event: PnLEvent): PnLState {
        return when (event) {
            is PnLEvent.Resume -> onResume(currentState)
            is PnLEvent.Pause -> onPause(currentState)
            is PnLEvent.LoadedFinishTrade -> onLoadedFinishTrade(currentState, event.finishTrades)
            is PnLEvent.FinishSelection -> onFinishTradeSelection(currentState, event.index)
        }
    }

    private fun onResume(currentState: PnLState): PnLState {
        finishTradesJob = viewModelScope.launch(Dispatchers.IO) {
            useCases.getFinishTrades().collect { finishTrades ->
                state.handleEvent(PnLEvent.LoadedFinishTrade(finishTrades))
            }
        }
        return currentState.copy(isLoading = true)
    }

    private fun onPause(currentState: PnLState): PnLState {
        finishTradesJob?.cancel()
        return currentState.copy(isLoading = false)
    }

    private fun onLoadedFinishTrade(
        currentState: PnLState,
        finishTrades: List<FinishTrade>
    ): PnLState {

        val symbols = mutableSetOf("All Finish Trades")
        symbols.addAll(finishTrades.map { it.symbol }.sorted())
        allFinishTrades = finishTrades
        val symbol = symbols.elementAt(currentState.finishTradeSelection)
        val selectedTrades = allFinishTrades.takeIf { currentState.finishTradeSelection == 0 }
            ?: allFinishTrades.filter { it.symbol == symbol }

        return calculateStats(currentState, selectedTrades).copy(
            isLoading = false,
            finishTrades = selectedTrades,
            finishTradeSymbols = symbols.toSet()
        )
    }

    private fun onFinishTradeSelection(currentState: PnLState, index: Int): PnLState {
        val symbol = currentState.finishTradeSymbols.elementAt(index)
        val selectedTrades = allFinishTrades.takeIf { index == 0 }
            ?: allFinishTrades.filter { it.symbol == symbol }

        return calculateStats(currentState, selectedTrades).copy(
            finishTrades = selectedTrades,
            finishTradeSelection = index
        )
    }

    private fun calculateStats(currentState: PnLState, finishTrades: List<FinishTrade>): PnLState {
        var totalPnL = .0f
        var winingTrades = 0
        var longTrades = 0
        var winingLongTrades = 0
        var shortTrades = 0
        var winingShortTrades = 0
        var totalProfit = .0
        var totalLoss = .0

        val pnlEntries = finishTrades.reversed().map {
            if (it.pnl.toFloat() > 0) {
                winingTrades++
                totalProfit += it.pnl
                if (it.type == TradeType.LONG) {
                    longTrades++
                    winingLongTrades++
                } else {
                    shortTrades++
                    winingShortTrades++
                }
            } else {
                totalLoss += it.pnl
                if (it.type == TradeType.LONG) {
                    longTrades++
                } else {
                    shortTrades++
                }
            }
            totalPnL += it.pnl.toFloat()
            totalPnL
        }

        val finalEntries = if (pnlEntries.size == 1) {
            val addingZero = pnlEntries.toMutableList()
            addingZero.add(0, .0f)
            addingZero
        } else {
            pnlEntries
        }

        return currentState.copy(
            totalPnL = totalPnL.toDouble(),
            totalTrades = finishTrades.size,
            winingTrades = winingTrades,
            longTrades = longTrades,
            winingLongTrades = winingLongTrades,
            shortTrades = shortTrades,
            winingShortTrades = winingShortTrades,
            totalProfit = totalProfit,
            totalLoss = abs(totalLoss),
            profitFactor = (totalProfit / abs(totalLoss)).takeIf { abs(totalLoss) > .0 } ?: 0.0,
            averagePnL = totalPnL.toDouble() / finishTrades.size,
            pnlEntries = finalEntries
        )
    }
}