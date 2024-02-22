package com.codeskraps.core.domain.mappers

import com.codeskraps.core.client.model.OrderDto
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.realm.model.OrderEntity

fun OrderDto.toOrderEntity(): OrderEntity {
    return OrderEntity(
        orderId = orderId,
        time = time,
        symbol = symbol,
        clientOrderId = clientOrderId,
        price = price,
        origQty = origQty,
        executedQty = executedQty,
        cummulativeQuoteQty = cummulativeQuoteQty,
        status = status,
        timeInForce = timeInForce,
        type = type,
        side = side,
        stopPrice = stopPrice,
        icebergQty = icebergQty,
        updateTime = updateTime,
        isWorking = isWorking,
        accountId = accountId,
        isIsolated = isIsolated,
        selfTradePreventionMode = selfTradePreventionMode
    )
}

fun OrderEntity.toOrder(): Order {
    return Order(
        orderId = orderId,
        time = time,
        symbol = symbol,
        clientOrderId = clientOrderId,
        price = price,
        origQty = origQty,
        executedQty = executedQty,
        cummulativeQuoteQty = cummulativeQuoteQty,
        status = status,
        timeInForce = timeInForce,
        type = type,
        side = side,
        stopPrice = stopPrice,
        icebergQty = icebergQty,
        updateTime = updateTime,
        isWorking = isWorking,
        accountId = accountId,
        isIsolated = isIsolated,
        selfTradePreventionMode = selfTradePreventionMode
    )
}