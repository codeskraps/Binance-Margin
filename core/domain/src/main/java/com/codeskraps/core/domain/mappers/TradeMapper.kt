package com.codeskraps.core.domain.mappers

import com.codeskraps.core.client.model.TradeDto
import com.codeskraps.core.domain.model.Trade
import com.codeskraps.core.realm.model.TradeEntity

fun TradeDto.toTradeEntity(): TradeEntity {
    val updatedSymbol = if (symbol.endsWith("USDC")) {
        symbol.replace("USDC", "USDT")
    } else if (symbol.endsWith("FDUSD")) {
        symbol.replace("FDUSD", "USDT")
    } else {
        symbol
    }
    return TradeEntity(
        orderId = orderId,
        symbol = updatedSymbol,
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