package com.codeskraps.feature.account.mvi

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.AssertSort
import com.codeskraps.core.domain.model.Asset
import com.codeskraps.core.domain.model.MarginAccount
import com.codeskraps.core.domain.model.Order
import com.codeskraps.core.domain.model.Ticker
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.feature.account.model.Entry
import com.codeskraps.core.domain.model.PnLTimeType
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs

data class AccountState(
    val isLoading: Boolean,
    val account: MarginAccount,
    val totalAssetOfUSDT: Double,
    val totalLiabilityOfUSDT: Double,
    val totalNetAssetOfUSDT: Double,
    val invested: Double,
    val pnl: Double,
    val pnlPercent: Double,
    val ticker: List<Ticker>,
    val entries: List<Entry>,
    val pnlEntries: List<Float>,
    val pnlTime: PnLTimeType,
    val assetsSort: AssertSort,
    val orders: List<Order>,
    val maxBorrow: Double,
    val utcTime: String
) {
    companion object {
        val initial = AccountState(
            isLoading = false,
            account = MarginAccount(),
            totalAssetOfUSDT = .0,
            totalLiabilityOfUSDT = .0,
            totalNetAssetOfUSDT = .0,
            invested = .0,
            pnl = .0,
            pnlPercent = .0,
            ticker = emptyList(),
            entries = emptyList(),
            pnlEntries = emptyList(),
            pnlTime = PnLTimeType.DAY,
            assetsSort = AssertSort.Value,
            orders = emptyList(),
            maxBorrow = .0,
            utcTime = ""
        )
    }

    val free: Double
        get() = runCatching {
            account.userAssets.first { it.asset == Constants.BASE_ASSET }.free
        }.getOrElse { .0 }

    val locked: Double
        get() = runCatching {
            account.userAssets.first { it.asset == Constants.BASE_ASSET }.locked
        }.getOrElse { .0 }

    val entryValueAssets: Double
        get() = runCatching {
            var totalValue = .0
            account.userAssets.filter { it.asset != Constants.BASE_ASSET }.forEach { asset ->
                totalValue += investedAsset(asset)
            }
            totalValue
        }.getOrElse { .0 }

    val valueAssets: Double
        get() = runCatching {
            var totalValue = .0
            account.userAssets.filter { it.asset != Constants.BASE_ASSET }.forEach { asset ->
                totalValue += value(asset)
            }
            totalValue
        }.getOrElse { .0 }

    val currentPnl: Double
        get() = runCatching {
            valueAssets - entryValueAssets
        }.getOrElse { .0 }

    val currentPnlPercent: Double
        get() = runCatching {
            (currentPnl / entryValueAssets) * 100
        }.getOrElse { .0 }

    fun valueAssetPercent(asset: Asset): Double {
        return runCatching {
            (value(asset) / valueAssets) * 100
        }.getOrElse { .0 }
    }

    fun value(asset: Asset): Double {
        return runCatching {
            if (asset.asset == Constants.BASE_ASSET) {
                asset.netAsset
            } else {
                abs(asset.netAsset) * ticker.first { it.symbol == "${asset.asset}${Constants.BASE_ASSET}" }.price
            }
        }.getOrElse { .0 }
    }

    fun price(asset: Asset): Double {
        return runCatching {
            if (asset.asset == Constants.BASE_ASSET) 1.0
            else ticker.first { it.symbol == "${asset.asset}${Constants.BASE_ASSET}" }.price
        }.getOrElse { .0 }
    }

    fun entry(asset: Asset): Double {
        return runCatching {
            entries.first { "${asset.asset}${Constants.BASE_ASSET}" == it.symbol }.entry
        }.getOrElse { .0 }
    }

    fun investedAsset(asset: Asset): Double {
        return runCatching {
            entry(asset) * abs(asset.netAsset)
        }.getOrElse { .0 }
    }

    fun pnLAsset(asset: Asset): Double {
        return runCatching {
            if (investedAsset(asset) == .0) .0
            else if (asset.netAsset > 0) { // LONG
                value(asset) - investedAsset(asset)
            } else { // SHORT
                investedAsset(asset) - value(asset)
            }
        }.getOrElse { .0 }
    }

    fun pnlAssetPercent(asset: Asset): Double {
        // TODO: Check is Long/SHORT
        return runCatching {
            val investedAsset = investedAsset(asset)
            if (investedAsset == .0) .0
            else if (asset.netAsset > 0) { // LONG
                ((value(asset) - investedAsset) / investedAsset) * 100
            } else { // SHORT
                ((investedAsset - value(asset)) / investedAsset) * 100
            }
        }.getOrElse { .0 }
    }

    fun orders(asset: Asset): List<Order> {
        return orders.filter { it.symbol == "${asset.asset}${Constants.BASE_ASSET}" }
    }

    /**
     * double collateral = 1000.0; // Example: Amount of collateral in USDT
     * double assetPrice = 4000.0; // Example: Current price of the asset in USDT
     * double leverageRatio = 3.0; // Example: Leverage ratio (e.g., 3x)
     * double maintenanceMargin = 0.10; // Example: Maintenance margin requirement (e.g., 10%)
     * double entryPrice = 4100.0; // Example: Entry price of the trade in USDT
     */
    fun calculateLiquidationPrice(
        collateral: Double,
        assetPrice: Double,
        leverageRatio: Double,
        maintenanceMargin: Double,
        entryPrice: Double
    ): Double {
        val totalDebt = collateral * leverageRatio / assetPrice
        return totalDebt / (collateral * (1 - maintenanceMargin)) + entryPrice
    }

    @Composable
    fun marginLevelColor(): Color {
        return colorResource(
            id =
            if (account.marginLevel >= 2)
                R.color.margin_level_green
            else if (account.marginLevel >= 1.25) {
                R.color.margin_level_amber
            } else {
                R.color.margin_level_red
            }
        )
    }

    @Composable
    fun pnlColor(pnl: Double): Color {
        return colorResource(
            id = if (pnl >= 1) R.color.margin_level_green
            else if (pnl >= 0) R.color.margin_level_amber
            else R.color.margin_level_red
        )
    }
}
