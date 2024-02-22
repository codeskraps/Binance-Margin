package com.codeskraps.feature.trades

import com.codeskraps.core.domain.usecases.trade.GetOrdersUseCase
import com.codeskraps.core.domain.usecases.trade.GetTradedSymbolsUseCase
import com.codeskraps.core.domain.usecases.trade.GetTradesUseCase
import javax.inject.Inject

class TradeUsesCases @Inject constructor(
    val getTradedSymbols: GetTradedSymbolsUseCase,
    val getTrades: GetTradesUseCase,
    val getOrders: GetOrdersUseCase
)