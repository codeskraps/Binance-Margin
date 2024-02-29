package com.codeskraps.feature.account.mvi

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.AssertSort
import com.codeskraps.core.domain.model.Asset
import com.codeskraps.core.domain.model.MarginAccount
import com.codeskraps.core.domain.model.PnL
import com.codeskraps.core.domain.model.Ticker
import com.codeskraps.core.domain.util.Constants
import com.codeskraps.feature.account.model.Entry
import com.codeskraps.core.domain.model.PnLTimeType

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
    val assetsSort: AssertSort
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
            assetsSort = AssertSort.Value
        )
    }

    val investedAssets: Double
        get() = runCatching {
            var totalValue = .0
            account.userAssets.filter { it.asset != Constants.BASE_ASSET }.forEach { asset ->
                totalValue += investedAsset(asset)
            }
            totalValue
        }.getOrElse { .0 }

    fun investedAssetPercent(asset: Asset): Double {
        return runCatching {
            (investedAsset(asset) / invested) * 100
        }.getOrElse { .0 }
    }

    fun value(asset: Asset): Double {
        return runCatching {
            if (asset.asset == Constants.BASE_ASSET) {
                asset.netAsset
            } else {
                asset.netAsset * ticker.first { it.symbol == "${asset.asset}${Constants.BASE_ASSET}" }.price
            }
        }.getOrElse { .0 }
    }

    fun price(asset: Asset): Double {
        return runCatching {
            ticker.first { it.symbol == "${asset.asset}${Constants.BASE_ASSET}" }.price
        }.getOrElse { .0 }
    }

    fun entry(asset: Asset): Double {
        return runCatching {
            entries.first { "${asset.asset}${Constants.BASE_ASSET}" == it.symbol }.entry
        }.getOrElse { .0 }
    }

    fun investedAsset(asset: Asset): Double {
        return runCatching {
            entry(asset) * asset.netAsset
        }.getOrElse { .0 }
    }

    fun pnLAsset(asset: Asset): Double {
        return runCatching {
            if (investedAsset(asset) == .0) .0
            else value(asset) - investedAsset(asset)
        }.getOrElse { .0 }
    }

    fun pnlAssetPercent(asset: Asset): Double {
        return runCatching {
            if (investedAsset(asset) == .0) .0
            else {
                val investedAsset = investedAsset(asset)
                ((value(asset) - investedAsset) / investedAsset) * 100
            }
        }.getOrElse { .0 }
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
