package com.codeskraps.feature.account

import androidx.lifecycle.viewModelScope
import com.codeskraps.core.domain.model.AssertSort
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.model.PnLTimeType
import com.codeskraps.core.domain.model.Ticker
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.core.domain.util.StateReducerViewModel
import com.codeskraps.feature.account.model.Entry
import com.codeskraps.feature.account.mvi.AccountAction
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val useCases: AccountUseCases
) : StateReducerViewModel<AccountState, AccountEvent, AccountAction>(AccountState.initial) {

    companion object {
        private val TAG = AccountViewModel::class.java.simpleName
        private const val UTC_FORMAT = "dd-MM-yyyy hh:mm:ss"
    }

    private var resumed = false
    private var accountJob: Job? = null
    private var tickerJob: Job? = null
    private var timeJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val assetsSort = useCases.getAssetsSort()
            state.handleEvent(AccountEvent.AssetsSortLoaded(assetsSort))
        }
    }

    override fun reduceState(currentState: AccountState, event: AccountEvent): AccountState {
        return when (event) {
            is AccountEvent.Resume -> onResume(currentState)
            is AccountEvent.AccountLoaded -> onAccountLoaded(currentState, event)
            is AccountEvent.Pause -> onPaused(currentState)
            is AccountEvent.LoadTicker -> onTicker(currentState)
            is AccountEvent.TickerLoaded -> onTickerLoaded(currentState, event.ticker)
            is AccountEvent.PnLLoaded -> onPnLLoaded(currentState, event.pnl)
            is AccountEvent.PnLTimeChanged -> onTimeChanged(currentState, event.time)
            is AccountEvent.AssetsSortLoaded -> onAssetsSortLoaded(currentState, event.assetsSort)
            is AccountEvent.OpenSymbol -> onOpenSymbol(currentState, event.symbol, event.entry)
            is AccountEvent.UTCTime -> onUTCTime(currentState, event.time)
            is AccountEvent.LoadSupperGuppy -> onLoadSupperGuppy(currentState, event.symbols)
        }
    }

    private fun onResume(currentState: AccountState): AccountState {
        resumed = true

        accountJob = viewModelScope.launch(Dispatchers.IO) {
            useCases.getMarginAccount().collect { account ->
                val symbols = account.userAssets.map { asset ->
                    "${asset.asset}${Constants.BASE_ASSET}"
                }

                val deferredResults = listOf(
                    async { useCases.getTickers(ArrayList(listOf("BTC${Constants.BASE_ASSET}"))) },
                    async { useCases.getInvested.invoke() },
                    async { useCases.getOrders() },
                    async { useCases.getMaxBorrow() },
                    *symbols
                        .map { symbol -> async { useCases.getEntryPrice(symbol) } }
                        .toTypedArray()
                )

                val results = awaitAll(*deferredResults.toTypedArray())

                val ticker = (results[0] as List<*>).filterIsInstance<Ticker>()
                val invested = results[1] as Double
                val orders = (results[2] as List<*>).filterIsInstance<Order>()
                val maxBorrow = results[3] as Double
                val entries = (results.subList(4, results.size) as List<*>)
                    .filterIsInstance<Double>()
                    .mapIndexed { index, entry ->
                        Entry(
                            symbol = symbols[index],
                            entry = entry
                        )
                    }

                val btcPrice = runCatching {
                    ticker.first { it.symbol == "BTC${Constants.BASE_ASSET}" }.price
                }.getOrElse { .0 }

                state.handleEvent(
                    AccountEvent.AccountLoaded(
                        account,
                        btcPrice,
                        invested,
                        entries,
                        orders,
                        maxBorrow
                    )
                )
                state.handleEvent(AccountEvent.LoadTicker)
                state.handleEvent(AccountEvent.PnLTimeChanged(useCases.getPnlTime()))
                state.handleEvent(AccountEvent.LoadSupperGuppy(symbols.toSet()))

                useCases.resetEntryPrices(symbols)
            }
        }

        timeJob = viewModelScope.launch(Dispatchers.IO) {
            while (resumed) {
                val date = Date(System.currentTimeMillis())
                val simpleDateFormat = SimpleDateFormat(UTC_FORMAT, Locale.getDefault())
                simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                state.handleEvent(AccountEvent.UTCTime(simpleDateFormat.format(date)))
                delay(1000L)
            }
        }
        return currentState.copy(isLoading = true)
    }

    private fun onPaused(currentState: AccountState): AccountState {
        resumed = false
        accountJob?.cancel()
        tickerJob?.cancel()
        return currentState
    }

    private fun onAccountLoaded(
        currentState: AccountState,
        event: AccountEvent.AccountLoaded
    ): AccountState {
        val account = event.account
        val totalNetAssetOfUSDT = account.totalNetAssetOfBtc * event.btcPrice

        val pnlPercent: Double = runCatching {
            ((totalNetAssetOfUSDT - event.invested) / event.invested) * 100.0
        }.getOrElse { .0 }

        return currentState.copy(
            isLoading = false,
            account = event.account,
            totalAssetOfUSDT = account.totalAssetOfBtc * event.btcPrice,
            totalLiabilityOfUSDT = account.totalLiabilityOfBtc * event.btcPrice,
            totalNetAssetOfUSDT = totalNetAssetOfUSDT,
            invested = event.invested,
            pnl = totalNetAssetOfUSDT - event.invested,
            pnlPercent = pnlPercent,
            entries = event.entries,
            orders = event.orders,
            maxBorrow = event.maxBorrow
        )
    }

    private fun onTicker(currentState: AccountState): AccountState {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500L)
            val symbols = ArrayList<String>()

            currentState.account.userAssets.forEach { asset ->
                symbols.add("${asset.asset}${Constants.BASE_ASSET}")
            }
            val ticker = useCases.getTickers(symbols)

            state.handleEvent(AccountEvent.TickerLoaded(ticker))
        }
        return currentState
    }

    private fun onTickerLoaded(currentState: AccountState, ticker: List<Ticker>): AccountState {
        if (resumed) {
            state.handleEvent(AccountEvent.LoadTicker)
        }
        return currentState.copy(ticker = ticker)
    }

    private fun onPnLLoaded(
        currentState: AccountState,
        pnl: List<Float>
    ): AccountState {
        return currentState.copy(pnlEntries = pnl)
    }

    private fun onTimeChanged(
        currentState: AccountState,
        time: PnLTimeType
    ): AccountState {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.putPnlTime(time)
            val pnl = useCases.getPnL(time).map { it.pnl.toFloat() }.toMutableList()
            pnl.add(currentState.pnl.toFloat())
            state.handleEvent(AccountEvent.PnLLoaded(pnl))
        }
        return currentState.copy(pnlTime = time)
    }

    private fun onAssetsSortLoaded(
        currentState: AccountState,
        assetsSort: AssertSort
    ): AccountState {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.putAssetsSort(assetsSort)
        }
        return currentState.copy(assetsSort = assetsSort)
    }

    private fun onOpenSymbol(
        currentState: AccountState,
        symbol: String,
        entry: Double
    ): AccountState {
        viewModelScope.launch(Dispatchers.IO) {
            actionChannel.send(AccountAction.OpenSymbol(symbol, entry))
        }
        return currentState
    }

    private fun onUTCTime(currentState: AccountState, time: String): AccountState {
        return currentState.copy(utcTime = time)
    }

    private fun onLoadSupperGuppy(currentState: AccountState, symbols: Set<String>): AccountState {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: Implement this
            //val superGuppy = useCases.getSuperGuppy(symbols)
            //state.handleEvent(AccountEvent.SuperGuppyLoaded(superGuppy))
        }
        return currentState
    }
}