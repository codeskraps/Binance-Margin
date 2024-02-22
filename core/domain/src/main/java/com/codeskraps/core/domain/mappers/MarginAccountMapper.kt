package com.codeskraps.core.domain.mappers

import com.codeskraps.core.client.model.MarginAccountDto
import com.codeskraps.core.domain.model.MarginAccount
import com.codeskraps.core.realm.model.MarginAccountEntity
import io.realm.kotlin.ext.toRealmList

fun MarginAccountDto.toMarginAccountEntity() =
    MarginAccountEntity(
        id = "1",
        marginLevel = marginLevel,
        totalAssetOfBtc = totalAssetOfBtc,
        totalLiabilityOfBtc = totalLiabilityOfBtc,
        totalNetAssetOfBtc = totalNetAssetOfBtc,
        borrowEnabled = borrowEnabled,
        collateralMarginLevel = CollateralMarginLevel,
        totalCollateralValueInUSDT = TotalCollateralValueInUSDT,
        tradeEnabled = tradeEnabled,
        transferEnabled = transferEnabled,
        accountType = accountType,
        userAssets = userAssets.map { it.toAssetEntity() }.toRealmList()
    )

fun MarginAccountEntity.toMarginAccount() = MarginAccount(
    marginLevel = marginLevel,
    totalAssetOfBtc = totalAssetOfBtc,
    totalLiabilityOfBtc = totalLiabilityOfBtc,
    totalNetAssetOfBtc = totalNetAssetOfBtc,
    borrowEnabled = borrowEnabled,
    collateralMarginLevel = collateralMarginLevel,
    totalCollateralValueInUSDT = totalCollateralValueInUSDT,
    tradeEnabled = tradeEnabled,
    transferEnabled = transferEnabled,
    accountType = accountType,
    userAssets = userAssets.map { it.toAsset() }
)