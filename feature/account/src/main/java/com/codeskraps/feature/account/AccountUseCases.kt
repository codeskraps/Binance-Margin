package com.codeskraps.feature.account

import com.codeskraps.core.domain.usecases.account.GetAssetsSortUseCase
import com.codeskraps.core.domain.usecases.account.GetEntryPriceUseCase
import com.codeskraps.core.domain.usecases.account.GetInvestedUseCase
import com.codeskraps.core.domain.usecases.account.GetMarginAccountUseCase
import com.codeskraps.core.domain.usecases.account.GetMaxBorrowUseCase
import com.codeskraps.core.domain.usecases.account.GetOrdersUseCase
import com.codeskraps.core.domain.usecases.account.GetPnLUseCase
import com.codeskraps.core.domain.usecases.account.GetPnlTimeUseCase
import com.codeskraps.core.domain.usecases.account.GetTickersUseCase
import com.codeskraps.core.domain.usecases.account.PutAssetsSortUseCase
import com.codeskraps.core.domain.usecases.account.PutPnlTimeUseCase
import com.codeskraps.core.domain.usecases.account.ResetEntryPricesUseCase
import javax.inject.Inject

class AccountUseCases @Inject constructor(
    val getMarginAccount: GetMarginAccountUseCase,
    val getTickers: GetTickersUseCase,
    val getInvested: GetInvestedUseCase,
    val getEntryPrice: GetEntryPriceUseCase,
    val getPnL: GetPnLUseCase,
    val getAssetsSort: GetAssetsSortUseCase,
    val putAssetsSort: PutAssetsSortUseCase,
    val putPnlTime: PutPnlTimeUseCase,
    val getPnlTime: GetPnlTimeUseCase,
    val getOrders: GetOrdersUseCase,
    val getMaxBorrow: GetMaxBorrowUseCase,
    val resetEntryPrices: ResetEntryPricesUseCase
)