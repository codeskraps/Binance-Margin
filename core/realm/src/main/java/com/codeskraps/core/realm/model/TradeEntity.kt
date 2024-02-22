package com.codeskraps.core.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class TradeEntity() : RealmObject {
    @PrimaryKey
    var orderId: Long = 0
    var symbol: String = ""
    var id: Long = 0
    var price: Double = 0.0
    var qty: Double = 0.0
    var quoteQty: Double = 0.0
    var commission: Double = 0.0
    var commissionAsset: String = ""
    var time: Long = 0
    var isBuyer: Boolean = false
    var isMaker: Boolean = false
    var isBestMatch: Boolean = false
    var isIsolated: Boolean = false

    constructor(
        symbol: String,
        id: Long,
        orderId: Long,
        price: Double,
        qty: Double,
        quoteQty: Double,
        commission: Double,
        commissionAsset: String,
        time: Long,
        isBuyer: Boolean,
        isMaker: Boolean,
        isBestMatch: Boolean,
        isIsolated: Boolean
    ) : this() {
        this.symbol = symbol
        this.id = id
        this.orderId = orderId
        this.price = price
        this.qty = qty
        this.quoteQty = quoteQty
        this.commission = commission
        this.commissionAsset = commissionAsset
        this.time = time
        this.isBuyer = isBuyer
        this.isMaker = isMaker
        this.isBestMatch = isBestMatch
        this.isIsolated = isIsolated
    }
}
