package com.codeskraps.core.domain.mappers

import com.codeskraps.core.domain.model.FinishTrade
import com.codeskraps.core.domain.model.TradeType
import com.codeskraps.core.realm.model.FinishedTradeEntity

fun FinishedTradeEntity.toFinishTrade() = FinishTrade(
    exitTime = exitTime,
    entryTime = entryTime,
    symbol = symbol,
    pnl = pnl,
    pnlPercent = pnlPercent,
    type = TradeType.entries[type],
    entryPrice = entryPrice,
    exitPrice = exitPrice,
    trades = trades
)

fun FinishTrade.toFinishedTradeEntity() = FinishedTradeEntity(
    exitTime = exitTime,
    entryTime = entryTime,
    symbol = symbol,
    pnl = pnl,
    pnlPercent = pnlPercent,
    type = type.ordinal,
    entryPrice = entryPrice,
    exitPrice = exitPrice,
    trades = trades
)