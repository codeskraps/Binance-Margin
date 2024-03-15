package ch.vilea.swisscom.feature.symbol

import com.codeskraps.core.domain.usecases.symbol.GetCandlesUseCase
import com.codeskraps.core.domain.usecases.symbol.GetChartTimeUseCase
import com.codeskraps.core.domain.usecases.symbol.GetOrdersUseCase
import com.codeskraps.core.domain.usecases.symbol.PutChartTimeUseCase
import javax.inject.Inject

class SymbolUseCases @Inject constructor(
    val getCandlesUseCase: GetCandlesUseCase,
    val getOrdersUseCase: GetOrdersUseCase,
    val getChartTimeUseCase: GetChartTimeUseCase,
    val putChartTimeUseCase: PutChartTimeUseCase
)