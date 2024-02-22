package com.codeskraps.core.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class PnLEntity() : RealmObject {
    @PrimaryKey
    var time: Long = 0
    var invested: Double = .0
    var totalAssetOfUSDT: Double = .0

    constructor(
        time: Long,
        invested: Double,
        totalAssetOfUSDT: Double,
    ) : this() {
        this.time = time
        this.invested = invested
        this.totalAssetOfUSDT = totalAssetOfUSDT
    }
}