package com.codeskraps.core.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class FinishedTradeEntity() : RealmObject {
    @PrimaryKey
    var id: Long = 0
    var exitTime: Long = 0
    var entryTime: Long = 0
    var symbol: String = ""
    var pnl: Double = .0
    var pnlPercent: Double = .0
    var type: Int = 0
    var entryPrice: Double = .0
    var exitPrice: Double = .0
    var trades: Int = 0

    constructor(
        exitTime: Long,
        entryTime: Long,
        symbol: String,
        pnl: Double,
        pnlPercent: Double,
        type: Int,
        entryPrice: Double,
        exitPrice: Double,
        trades: Int
    ) : this() {
        this.id = exitTime
        this.exitTime = exitTime
        this.entryTime = entryTime
        this.symbol = symbol
        this.pnl = pnl
        this.pnlPercent = pnlPercent
        this.type = type
        this.entryPrice = entryPrice
        this.exitPrice = exitPrice
        this.trades = trades
    }
}