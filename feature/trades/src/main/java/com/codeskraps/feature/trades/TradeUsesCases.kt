package com.codeskraps.feature.trades

import com.codeskraps.core.domain.usecases.trade.GetOrdersUseCase
import com.codeskraps.core.domain.usecases.trade.GetTradedSymbolsUseCase
import com.codeskraps.core.domain.usecases.trade.GetTradesUseCase
import com.codeskraps.core.domain.usecases.trade.GetTransfersUseCase
import com.codeskraps.core.domain.usecases.trade.UpdatePriceUseCase
import javax.inject.Inject

class TradeUsesCases @Inject constructor(
    val getTradedSymbols: GetTradedSymbolsUseCase,
    val getTrades: GetTradesUseCase,
    val getOrders: GetOrdersUseCase,
    val getTransfers: GetTransfersUseCase,
    val updatePriceUseCase: UpdatePriceUseCase
)