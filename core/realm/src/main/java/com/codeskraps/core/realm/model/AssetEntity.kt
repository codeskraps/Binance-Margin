package com.codeskraps.core.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class AssetEntity() : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var asset: String = ""
    var free: Double = .0
    var locked: Double = .0
    var borrowed: Double = .0
    var interest: Double = .0
    var netAsset: Double = .0

    constructor(
        id: ObjectId,
        asset: String,
        free: Double,
        locked: Double,
        borrowed: Double,
        interest: Double,
        netAsset: Double,
    ) : this() {
        this.id = id
        this.asset = asset
        this.free = free
        this.locked = locked
        this.borrowed = borrowed
        this.interest = interest
        this.netAsset = netAsset
    }
}