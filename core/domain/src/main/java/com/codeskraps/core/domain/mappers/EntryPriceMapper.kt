package com.codeskraps.core.domain.mappers

import com.codeskraps.core.domain.model.EntryPrice
import com.codeskraps.core.realm.model.EntryPriceEntity

fun EntryPrice.toEntryPriceEntity() = EntryPriceEntity(
    symbol = symbol,
    price = price,
    lastTrade = lastTrade
)

fun EntryPriceEntity.toEntryPrice() = EntryPrice(
    symbol = symbol,
    price = price,
    lastTrade = lastTrade
)