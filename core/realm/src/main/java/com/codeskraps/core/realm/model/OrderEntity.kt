package com.codeskraps.core.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class OrderEntity() : RealmObject {
    @PrimaryKey
    var orderId: Long = 0
    var time: Long = 0
    var symbol: String = ""
    var clientOrderId: String = ""
    var price: String = ""
    var origQty: String = ""
    var executedQty: String = ""
    var cummulativeQuoteQty: String = ""
    var status: String = ""
    var timeInForce: String = ""
    var type: String = ""
    var side: String = ""
    var stopPrice: String = ""
    var icebergQty: String = ""
    var updateTime: Long = 0
    var isWorking: Boolean = false
    var isIsolated: Boolean = false
    var selfTradePreventionMode: String = ""

    constructor(
        symbol: String,
        orderId: Long,
        clientOrderId: String,
        price: String,
        origQty: String,
        executedQty: String,
        cummulativeQuoteQty: String,
        status: String,
        timeInForce: String,
        type: String,
        side: String,
        stopPrice: String,
        icebergQty: String,
        time: Long,
        updateTime: Long,
        isWorking: Boolean,
        isIsolated: Boolean,
        selfTradePreventionMode: String
    ) : this() {
        this.symbol = symbol
        this.orderId = orderId
        this.clientOrderId = clientOrderId
        this.price = price
        this.origQty = origQty
        this.executedQty = executedQty
        this.cummulativeQuoteQty = cummulativeQuoteQty
        this.status = status
        this.timeInForce = timeInForce
        this.type = type
        this.side = side
        this.stopPrice = stopPrice
        this.icebergQty = icebergQty
        this.time = time
        this.updateTime = updateTime
        this.isWorking = isWorking
        this.isIsolated = isIsolated
        this.selfTradePreventionMode = selfTradePreventionMode
    }
}
