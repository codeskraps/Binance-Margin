package com.codeskraps.core.client

import android.app.Application
import android.content.Context
import javax.inject.Inject

class BinanceStore @Inject constructor(
    private val context: Application
) {

    companion object {
        private const val storageName = "BinanceStorage"
        private const val prefLastTransferParsed = "lastTransferParsed"
        private const val prefInvested = "invested"
        private const val prefTradedAssets = "tradedAssets"
    }

    private val prefs by lazy {
        context.getSharedPreferences(
            storageName,
            Context.MODE_PRIVATE
        )
    }

    var lastTransferParsed: Long
        get() = prefs.getLong(prefLastTransferParsed, 0)
        set(value) = prefs.edit().putLong(prefLastTransferParsed, value).apply()

    var invested: Float
        get() = prefs.getFloat(prefInvested, .0f)
        set(value) = prefs.edit().putFloat(prefInvested, value).apply()

    var tradedAssets: Set<String>
        get() = prefs.getStringSet(prefTradedAssets, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(prefTradedAssets, value).apply()
}