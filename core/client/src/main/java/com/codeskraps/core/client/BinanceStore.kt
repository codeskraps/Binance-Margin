package com.codeskraps.core.client

import android.app.Application
import android.content.Context
import javax.inject.Inject

class BinanceStore @Inject constructor(
    private val context: Application
) {

    companion object {
        private const val START_TIME = 1707414465000
        private const val STORAGE_NAME = "BinanceStorage"
        private const val PREF_LAST_TRANSFER_PARSED = "lastTransferParsed"
        private const val PREF_INVESTED = "invested"
        private const val PREF_TRADED_ASSETS = "tradedAssets"
        private const val PREF_API_KEY = "prefApiKey"
        private const val PREF_SECRET_KEY = "prefSecretKey"
        private const val PREF_START_DATE = "prefStartDate"
        private const val PREF_PNL_TIME_TYPE = "prefPnlTimeType"
        private const val PREF_CHART_TIME_TYPE = "prefChartTimeType"
        private const val PREF_ASSET_SORT = "prefAssetsSort"
        private const val PREF_LAST_ORDER_CHECKED = "lastOrderChecked"
        private const val PREF_BAD_SYMBOLS = "prefBadSymbols"
    }

    private val prefs by lazy {
        context.getSharedPreferences(
            STORAGE_NAME,
            Context.MODE_PRIVATE
        )
    }

    var lastTransferParsed: Long
        get() = prefs.getLong(PREF_LAST_TRANSFER_PARSED, 0)
        set(value) = prefs.edit().putLong(PREF_LAST_TRANSFER_PARSED, value).apply()

    var invested: Float
        get() = prefs.getFloat(PREF_INVESTED, .0f)
        set(value) = prefs.edit().putFloat(PREF_INVESTED, value).apply()

    var tradedAssets: Set<String>
        get() = prefs.getStringSet(PREF_TRADED_ASSETS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(PREF_TRADED_ASSETS, value).apply()

    var apiKey: String
        get() = prefs.getString(PREF_API_KEY, BuildConfig.API_KEY) ?: BuildConfig.API_KEY
        set(value) = prefs.edit().putString(PREF_API_KEY, value).apply()

    var secretKey: String
        get() = prefs.getString(PREF_SECRET_KEY, BuildConfig.SECRET_KEY) ?: BuildConfig.SECRET_KEY
        set(value) = prefs.edit().putString(PREF_SECRET_KEY, value).apply()

    var startDate: Long
        get() = prefs.getLong(PREF_START_DATE, START_TIME)
        set(value) = prefs.edit().putLong(PREF_START_DATE, value).apply()

    var pnlTimeType: String
        get() = prefs.getString(PREF_PNL_TIME_TYPE, "1d") ?: "1d"
        set(value) = prefs.edit().putString(PREF_PNL_TIME_TYPE, value).apply()

    var chartTimeType: String
        get() = prefs.getString(PREF_CHART_TIME_TYPE, "1d") ?: "1d"
        set(value) = prefs.edit().putString(PREF_CHART_TIME_TYPE, value).apply()

    var assetsSort: String
        get() = prefs.getString(PREF_ASSET_SORT, "Value") ?: "Value"
        set(value) = prefs.edit().putString(PREF_ASSET_SORT, value).apply()

    var lastOrderChecked: Long
        get() = prefs.getLong(PREF_LAST_ORDER_CHECKED, 0)
        set(value) = prefs.edit().putLong(PREF_LAST_ORDER_CHECKED, value).apply()

    var badSymbols: Set<String>
        get() = prefs.getStringSet(PREF_BAD_SYMBOLS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(PREF_BAD_SYMBOLS, value).apply()
}