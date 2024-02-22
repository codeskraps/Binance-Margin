package com.codeskraps.feature.account

import androidx.lifecycle.viewModelScope
import com.codeskraps.core.domain.model.PnL
import com.codeskraps.core.domain.model.Ticker
import com.codeskraps.core.domain.usecases.GetEntryPriceUseCase
import com.codeskraps.core.domain.usecases.GetInvestedUseCase
import com.codeskraps.core.domain.usecases.GetMarginAccountUseCase
import com.codeskraps.core.domain.usecases.GetPnLUseCase
import com.codeskraps.core.domain.usecases.GetTickersUseCase
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.codeskraps.feature.account.model.Entry
import com.codeskraps.core.domain.model.PnLTimeType
import com.codeskraps.feature.account.mvi.AccountAction
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getMarginAccount: GetMarginAccountUseCase,
    private val getTickers: GetTickersUseCase,
    private val getInvested: GetInvestedUseCase,
    private val getEntryPrice: GetEntryPriceUseCase,
    private val getPnL: GetPnLUseCase
) : StateReducerViewModel<AccountState, AccountEvent, AccountAction>() {

    companion object {
        private val TAG = AccountViewModel::class.java.simpleName
    }

    override fun initState(): AccountState = AccountState.initial

    private var resumed = false

    override fun reduceState(currentState: AccountState, event: AccountEvent): AccountState {
        return when (event) {
            is AccountEvent.Resume -> onResume(currentState)
            is AccountEvent.AccountLoaded -> onLoaded(currentState, event)
            is AccountEvent.Pause -> onPaused(currentState)
            is AccountEvent.Ticker -> onTicker(currentState)
            is AccountEvent.TickerLoaded -> onTickerLoaded(currentState, event.ticker)
            is AccountEvent.PnLLoaded -> onPnLLoaded(currentState, event.pnl)
            is AccountEvent.PnLTimeChanged -> onTimeChanged(currentState, event.time)
        }
    }

    private fun onResume(currentState: AccountState): AccountState {
        resumed = true

        viewModelScope.launch(Dispatchers.IO) {
            getMarginAccount().collect { account ->
                val symbols = ArrayList<String>()

                account.userAssets.forEach { asset ->
                    symbols.add("${asset.asset}${Constants.BASE_ASSET}")
                }

                val deferredTicker = async { getTickers(symbols) }
                //val priceIndex = client.kLines(symbols[0], Interval.DAILY, 10)
                val deferredInvested = async { getInvested() }

                val deferredEntries = ArrayList<Deferred<Double>>()

                account.userAssets.forEach { asset ->
                    val symbol = "${asset.asset}${Constants.BASE_ASSET}"
                    deferredEntries.add(async { getEntryPrice(symbol) })
                }

                val ticker = deferredTicker.await()
                val invested = deferredInvested.await()
                val entries = ArrayList<Entry>()
                deferredEntries.awaitAll().forEachIndexed { index, entry ->
                    val symbol = "${account.userAssets[index].asset}${Constants.BASE_ASSET}"
                    entries.add(
                        Entry(
                            symbol = symbol,
                            entry = entry
                        )
                    )
                }

                state.handleEvent(AccountEvent.AccountLoaded(account, ticker, invested, entries))
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val pnl = getPnL(currentState.pnlTime)
            state.handleEvent(AccountEvent.PnLLoaded(pnl))

        }
        return currentState.copy(isLoading = true)
    }

    private fun onPaused(currentState: AccountState): AccountState {
        resumed = false
        return currentState
    }

    private fun onLoaded(
        currentState: AccountState,
        event: AccountEvent.AccountLoaded
    ): AccountState {
        if (resumed) {
            state.handleEvent(AccountEvent.Ticker)
        }

        val account = event.account
        val ticker = event.ticker
        val btcPrice = runCatching {
            ticker.first { it.symbol == "BTC${Constants.BASE_ASSET}" }.price
        }.getOrElse { .0 }
        val totalNetAssetOfUSDT = account.totalNetAssetOfBtc * btcPrice

        val pnlPercent: Double = runCatching {
            val difference = totalNetAssetOfUSDT - event.invested
            val average = (totalNetAssetOfUSDT + event.invested) / 2
            (difference / average) * 100
        }.getOrElse { .0 }

        return currentState.copy(
            isLoading = false,
            account = event.account,
            totalAssetOfUSDT = account.totalAssetOfBtc * btcPrice,
            totalLiabilityOfUSDT = account.totalLiabilityOfBtc * btcPrice,
            totalNetAssetOfUSDT = totalNetAssetOfUSDT,
            ticker = event.ticker,
            invested = event.invested,
            pnl = totalNetAssetOfUSDT - event.invested,
            pnlPercent = pnlPercent,
            entries = event.entries
        )
    }

    private fun onTicker(currentState: AccountState): AccountState {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500L)
            val symbols = ArrayList<String>()

            currentState.account.userAssets.forEach { asset ->
                symbols.add("${asset.asset}${Constants.BASE_ASSET}")
            }
            val ticker = getTickers(symbols)

            state.handleEvent(AccountEvent.TickerLoaded(ticker))
        }
        return currentState
    }

    private fun onTickerLoaded(currentState: AccountState, ticker: List<Ticker>): AccountState {
        if (resumed) {
            state.handleEvent(AccountEvent.Ticker)
        }
        return currentState.copy(ticker = ticker)
    }

    private fun onPnLLoaded(
        currentState: AccountState,
        pnl: List<PnL>
    ): AccountState {
        return currentState.copy(pnlEntries = pnl)
    }

    private fun onTimeChanged(
        currentState: AccountState,
        time: PnLTimeType
    ): AccountState {
        viewModelScope.launch(Dispatchers.IO) {
            val pnl = getPnL(time)
            state.handleEvent(AccountEvent.PnLLoaded(pnl))
        }
        return currentState.copy(pnlTime = time)
    }
}