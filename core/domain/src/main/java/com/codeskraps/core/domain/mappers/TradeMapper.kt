package com.codeskraps.core.domain.mappers

import com.codeskraps.core.client.model.TradeDto
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.realm.model.TradeEntity

fun TradeDto.toTradeEntity(): TradeEntity {
    return TradeEntity(
        orderId = orderId,
        symbol = symbol,
        id = id,
        price = price,
        qty = qty,
        quoteQty = quoteQty,
        commission = commission,
        commissionAsset = commissionAsset,
        time = time,
        isBuyer = isBuyer,
        isMaker = isMaker,
        isBestMatch = isBestMatch,
        isIsolated = isIsolated
    )
}

fun TradeEntity.toTrade(): Trade {
    return Trade(
        orderId = orderId,
        symbol = symbol,
        id = id,
        price = price,
        qty = qty,
        quoteQty = quoteQty,
        commission = commission,
        commissionAsset = commissionAsset,
        time = time,
        isBuyer = isBuyer,
        isMaker = isMaker,
        isBestMatch = isBestMatch,
        isIsolated = isIsolated
    )
}