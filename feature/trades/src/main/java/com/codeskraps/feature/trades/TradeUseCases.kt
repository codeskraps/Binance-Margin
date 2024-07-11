package com.codeskraps.feature.trades

import com.codeskraps.core.domain.usecases.trade.DeleteOrderUseCase
import com.codeskraps.core.domain.usecases.trade.GetOrdersUseCase
import com.codeskraps.core.domain.usecases.trade.GetTradesUseCase
import com.codeskraps.core.domain.usecases.trade.GetTransfersUseCase
import com.codeskraps.core.domain.usecases.trade.UpdatePriceUseCase
import javax.inject.Inject

class TradeUseCases @Inject constructor(
    val getTrades: GetTradesUseCase,
    val getOrders: GetOrdersUseCase,
    val getTransfers: GetTransfersUseCase,
    val updatePrice: UpdatePriceUseCase,
    val deleteOrder: DeleteOrderUseCase
)