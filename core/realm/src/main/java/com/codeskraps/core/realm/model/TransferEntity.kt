package com.codeskraps.core.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class TransferEntity() : RealmObject {
    @PrimaryKey
    var id: Long = 0
    var timestamp: Long = 0
    var asset: String = ""
    var amount: Double = 0.0
    var type: String = ""
    var status: String = ""
    var transFrom: String = ""
    var transTo: String = ""
    var price: Double = 0.0

    constructor(
        id: Long,
        timestamp: Long,
        asset: String,
        amount: Double,
        type: String,
        status: String,
        transFrom: String,
        transTo: String,
        price: Double
    ) : this() {
        this.id = id
        this.timestamp = timestamp
        this.asset = asset
        this.amount = amount
        this.type = type
        this.status = status
        this.transFrom = transFrom
        this.transTo = transTo
        this.price = price
    }
}