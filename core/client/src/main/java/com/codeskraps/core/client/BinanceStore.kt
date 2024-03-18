package com.codeskraps.core.client

import android.app.Application
import android.content.Context
import javax.inject.Inject

class BinanceStore @Inject constructor(
    private val context: Application
) {

    companion object {
        private const val START_TIME = 1707414465000
        private const val storageName = "BinanceStorage"
        private const val prefLastTransferParsed = "lastTransferParsed"
        private const val prefInvested = "invested"
        private const val prefTradedAssets = "tradedAssets"
        private const val prefApiKey = "prefApiKey"
        private const val prefSecretKey = "prefSecretKey"
        private const val prefStartDate = "prefStartDate"
        private const val prefPnlTimeType = "prefPnlTimeType"
        private const val prefChartTimeType = "prefChartTimeType"
        private const val prefAssetsSort = "prefAssetsSort"
        private const val prefLastOrderChecked = "lastOrderChecked"
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

    var apiKey: String
        get() = prefs.getString(prefApiKey, BuildConfig.API_KEY) ?: BuildConfig.API_KEY
        set(value) = prefs.edit().putString(prefApiKey, value).apply()

    var secretKey: String
        get() = prefs.getString(prefSecretKey, BuildConfig.SECRET_KEY) ?: BuildConfig.SECRET_KEY
        set(value) = prefs.edit().putString(prefSecretKey, value).apply()

    var startDate: Long
        get() = prefs.getLong(prefStartDate, START_TIME)
        set(value) = prefs.edit().putLong(prefStartDate, value).apply()

    var pnlTimeType: String
        get() = prefs.getString(prefPnlTimeType, "1d") ?: "1d"
        set(value) = prefs.edit().putString(prefPnlTimeType, value).apply()

    var chartTimeType: String
        get() = prefs.getString(prefChartTimeType, "1d") ?: "1d"
        set(value) = prefs.edit().putString(prefChartTimeType, value).apply()

    var assetsSort: String
        get() = prefs.getString(prefAssetsSort, "Value") ?: "Value"
        set(value) = prefs.edit().putString(prefAssetsSort, value).apply()

    var lastOrderChecked: Long
        get() = prefs.getLong(prefLastOrderChecked, 0)
        set(value) = prefs.edit().putLong(prefLastOrderChecked, value).apply()
}