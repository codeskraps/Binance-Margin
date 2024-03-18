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
        price = runCatching { price.toDouble() }.getOrElse { .0 },
        origQty = runCatching { origQty.toDouble() }.getOrElse { .0 },
        executedQty = runCatching { executedQty.toDouble() }.getOrElse { .0 },
        cummulativeQuoteQty = runCatching { cummulativeQuoteQty.toDouble() }.getOrElse { .0 },
        status = status,
        timeInForce = timeInForce,
        type = type,
        side = side,
        stopPrice = stopPrice,
        icebergQty = icebergQty,
        updateTime = updateTime,
        isWorking = isWorking,
        isIsolated = isIsolated,
        selfTradePreventionMode = selfTradePreventionMode
    )
}