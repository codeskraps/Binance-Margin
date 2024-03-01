package com.codeskraps.core.domain.util

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.Asset

object StateUtil {
    fun logo(asset: Asset): Int {
        return logo(asset.asset)
    }

    fun logo(symbol: String): Int {
        val asset = symbol
            .takeIf { !it.endsWith(BinanceClient.BASE_ASSET) || it == BinanceClient.BASE_ASSET }
            ?: symbol.substring(0, symbol.length - BinanceClient.BASE_ASSET.length)

        return when (asset) {
            "SOL" -> R.drawable.solana_sol_logo
            "BTC" -> R.drawable.bitcoin_btc_logo
            "ETH" -> R.drawable.ethereum_eth_logo
            "LINK" -> R.drawable.chainlink_link_logo
            "ADA" -> R.drawable.cardano_ada_logo
            "USDT" -> R.drawable.tether_usdt_logo
            "BNB" -> R.drawable.bnb_bnb_logo
            "FIL" -> R.drawable.filecoin_fil_logo
            "ATOM" -> R.drawable.cosmos_atom_logo
            "STX" -> R.drawable.stacks_stx_logo
            "SUI" -> R.drawable.sui_sui_logo
            "MATIC" -> R.drawable.polygon_matic_logo
            "ARB" -> R.drawable.arbitrum_arb_logo
            "INJ" -> R.drawable.injective_inj_logo
            "AVAX" -> R.drawable.avalanche_avax_logo
            "TRX" -> R.drawable.tron_trx_logo
            "RUNE" -> R.drawable.thorchain_rune_logo
            "RNDR" -> R.drawable.render_token_logo
            "MANTA" -> R.drawable.manta_network_logo
            "BONK" -> R.drawable.bonk_logo
            "AR" -> R.drawable.arweave_ar_logo
            "DOT" -> R.drawable.polkadot_new_dot_logo
            "FTM" -> R.drawable.fantom_ftm_logo
            "VET" -> R.drawable.vechain_vet_logo
            "ALGO" -> R.drawable.algorand_algo_logo
            "SUPER" -> R.drawable.super_farm_logo
            "PYR" -> R.drawable.vulkan_forged_pyr_logo
            "CAKE" -> R.drawable.pancakeswap_cake_logo
            "XRP" -> R.drawable.xrp_xrp_logo
            else -> 0
        }
    }

    fun decimal(asset: Asset): Int {
        return decimal(asset.asset)
    }

    fun decimal(symbol: String): Int {
        return when (symbol.replace(BinanceClient.BASE_ASSET, "")) {
            "BONK" -> 8
            "MANTA", "VET" -> 5
            "ADA", "JUP", "MATIC", "SUI", "ARB", "STX", "XAI", "TRX", "FTM", "ALGO", "SUPER", "XRP" -> 4
            "LINK", "FIL", "ATOM", "RNDR", "AR", "DOT", "PYR", "CAKE" -> 3
            "BNB", "BTC", "ETH" -> 1
            else -> 2
        }
    }
}