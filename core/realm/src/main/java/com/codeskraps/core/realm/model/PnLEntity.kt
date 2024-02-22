package com.codeskraps.core.realm.model

interface PnLEntity {
    fun time(): Long
    fun invested(): Double
    fun totalAssetOfUSDT(): Double
}