package com.codeskraps.core.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class PnLHourlyEntity() : RealmObject, PnLEntity {
    @PrimaryKey
    var id: Long = 0
    var time: Long = 0
    var invested: Double = .0
    var totalAssetOfUSDT: Double = .0

    constructor(
        id: Long,
        time: Long,
        invested: Double,
        totalAssetOfUSDT: Double,
    ) : this() {
        this.id = id
        this.time = time
        this.invested = invested
        this.totalAssetOfUSDT = totalAssetOfUSDT
    }

    override fun time(): Long = time
    override fun invested(): Double = invested

    override fun totalAssetOfUSDT(): Double = totalAssetOfUSDT
}