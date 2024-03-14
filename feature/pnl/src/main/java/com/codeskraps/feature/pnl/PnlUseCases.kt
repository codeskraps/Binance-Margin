package com.codeskraps.feature.pnl

import com.codeskraps.core.domain.usecases.pnl.GetFinishTradesUseCase
import javax.inject.Inject

class PnlUseCases @Inject constructor(
    val getFinishTrades: GetFinishTradesUseCase
)