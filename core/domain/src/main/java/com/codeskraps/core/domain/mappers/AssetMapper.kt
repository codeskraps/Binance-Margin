package com.codeskraps.core.domain.mappers

import com.codeskraps.core.domain.model.Asset
import com.codeskraps.core.realm.model.AssetEntity
import org.mongodb.kbson.ObjectId


fun com.codeskraps.core.client.model.AssetDto.toAssetEntity() =
    AssetEntity(
        id = ObjectId(),
        asset = asset,
        free = free,
        locked = locked,
        borrowed = borrowed,
        interest = interest,
        netAsset = netAsset
    )

fun AssetEntity.toAsset() = Asset(
    asset = asset,
    free = free,
    locked = locked,
    borrowed = borrowed,
    interest = interest,
    netAsset = netAsset
)

