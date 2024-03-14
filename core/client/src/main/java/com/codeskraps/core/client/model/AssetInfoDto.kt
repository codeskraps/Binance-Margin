package com.codeskraps.core.client.model

data class AssetInfoDto(
    val assetName: String,
    val assetFullName: String,
    val isBorrowable: Boolean,
    val isMortgageable: Boolean,
    val userMinBorrow: String,
    val userMinRepay: String
)
