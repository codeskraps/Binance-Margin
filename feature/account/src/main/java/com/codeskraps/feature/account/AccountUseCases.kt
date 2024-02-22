package com.codeskraps.feature.account

import com.codeskraps.core.domain.usecases.account.GetAssetsSortUseCase
import com.codeskraps.core.domain.usecases.account.GetEntryPriceUseCase
import com.codeskraps.core.domain.usecases.account.GetInvestedUseCase
import com.codeskraps.core.domain.usecases.account.GetMarginAccountUseCase
import com.codeskraps.core.domain.usecases.account.GetPnLUseCase
import com.codeskraps.core.domain.usecases.account.GetPnlTimeUseCase
import com.codeskraps.core.domain.usecases.account.GetTickersUseCase
import com.codeskraps.core.domain.usecases.account.PutAssetsSortUseCase
import com.codeskraps.core.domain.usecases.account.PutPnlTimeUseCase
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
    val getPnlTimeUseCase: GetPnlTimeUseCase
)