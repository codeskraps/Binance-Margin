package com.codeskraps.core.realm.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class MarginAccountEntity() : RealmObject {
    @PrimaryKey
    var id: String = ""
    var marginLevel: Double = .0
    var totalAssetOfBtc: Double = .0
    var totalLiabilityOfBtc: Double = .0
    var totalNetAssetOfBtc: Double = .0
    var borrowEnabled: Boolean = false
    var collateralMarginLevel: Double = .0
    var totalCollateralValueInUSDT: Double = .0
    var tradeEnabled: Boolean = false
    var transferEnabled: Boolean = false
    var accountType: String = ""
    var userAssets: RealmList<AssetEntity> = realmListOf()

    constructor(
        id: String,
        marginLevel: Double,
        totalAssetOfBtc: Double,
        totalLiabilityOfBtc: Double,
        totalNetAssetOfBtc: Double,
        borrowEnabled: Boolean,
        collateralMarginLevel: Double,
        totalCollateralValueInUSDT: Double,
        tradeEnabled: Boolean,
        transferEnabled: Boolean,
        accountType: String,
        userAssets: RealmList<AssetEntity>,
    ) : this() {
        this.id = id
        this.marginLevel = marginLevel
        this.totalAssetOfBtc = totalAssetOfBtc
        this.totalLiabilityOfBtc = totalLiabilityOfBtc
        this.totalNetAssetOfBtc = totalNetAssetOfBtc
        this.borrowEnabled = borrowEnabled
        this.collateralMarginLevel = collateralMarginLevel
        this.totalCollateralValueInUSDT = totalCollateralValueInUSDT
        this.tradeEnabled = tradeEnabled
        this.transferEnabled = transferEnabled
        this.accountType = accountType
        this.userAssets = userAssets
    }
}
