package com.codeskraps.core.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class EntryPriceEntity() : RealmObject {
    @PrimaryKey
    var id: String = ""
    var symbol: String = ""
    var price: Double = .0
    var lastTrade: Long = 0

    constructor(
        symbol: String,
        price: Double,
        lastTrade: Long
    ) : this() {
        this.id = symbol
        this.symbol = symbol
        this.price = price
        this.lastTrade = lastTrade
    }
}