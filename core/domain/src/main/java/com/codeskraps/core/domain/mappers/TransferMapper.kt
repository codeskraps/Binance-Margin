package com.codeskraps.core.domain.mappers

import com.codeskraps.core.client.model.TransferDto
import com.codeskraps.core.domain.model.Transfer
import com.codeskraps.core.realm.model.TransferEntity

fun TransferEntity.toTransfer() = Transfer(
    txId = id,
    asset = asset,
    amount = amount,
    transFrom = transFrom,
    transTo = transTo,
    timestamp = timestamp,
    type = type,
    status = status,
    price = price
)

fun TransferDto.toTransferEntity() = TransferEntity(
    id = txId,
    asset = asset,
    amount = amount,
    transFrom = transFrom,
    transTo = transTo,
    timestamp = timestamp,
    type = type,
    status = status,
    price = 0.0
)

fun Transfer.toTransferEntity() = TransferEntity(
    id = txId,
    asset = asset,
    amount = amount,
    transFrom = transFrom,
    transTo = transTo,
    timestamp = timestamp,
    type = type,
    status = status,
    price = price
)