package com.codeskraps.feature.symbol

import com.codeskraps.core.domain.usecases.account.GetRealmEntryPriceUseCase
import com.codeskraps.core.domain.usecases.symbol.GetCandlesUseCase
import com.codeskraps.core.domain.usecases.symbol.GetChartTimeUseCase
import com.codeskraps.core.domain.usecases.symbol.GetOrdersUseCase
import com.codeskraps.core.domain.usecases.symbol.PutChartTimeUseCase
import com.codeskraps.core.domain.usecases.symbol.RSIUseCase
import com.codeskraps.core.domain.usecases.symbol.StochRSIUseCase
import com.codeskraps.core.domain.usecases.symbol.SuperGuppyUseCase
import javax.inject.Inject

class SymbolUseCases @Inject constructor(
    val getCandles: GetCandlesUseCase,
    val getOrders: GetOrdersUseCase,
    val getChartTime: GetChartTimeUseCase,
    val putChartTime: PutChartTimeUseCase,
    val superGuppy: SuperGuppyUseCase,
    val rsi: RSIUseCase,
    val stochRSI: StochRSIUseCase,
    val getRealmEntryPrice: GetRealmEntryPriceUseCase
)