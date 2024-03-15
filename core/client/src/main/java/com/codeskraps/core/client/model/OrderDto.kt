package com.codeskraps.core.client.model

data class OrderDto(
    val symbol: String,
    val orderId: Long,
    val clientOrderId: String,
    val price: String,
    val origQty: String,
    val executedQty: String,
    val cummulativeQuoteQty: String,
    val status: String,
    val timeInForce: String,
    val type: String,
    val side: String,
    val stopPrice: String,
    val icebergQty: String,
    val time: Long,
    val updateTime: Long,
    val isWorking: Boolean,
    val isIsolated: Boolean,
    val selfTradePreventionMode: String
)
