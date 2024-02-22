package com.codeskraps.core.domain.util

import com.codeskraps.core.domain.R

object StateUtil {
    fun logo(asset: com.codeskraps.core.domain.model.Asset): Int {
        return when (asset.asset) {
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
            else -> 0
        }
    }

    fun logo(symbol: String): Int {
        return when (symbol) {
            "SOL${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.solana_sol_logo
            "BTC${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.bitcoin_btc_logo
            "ETH${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.ethereum_eth_logo
            "LINK${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.chainlink_link_logo
            "ADA${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.cardano_ada_logo
            "USDT${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.tether_usdt_logo
            "BNB${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.bnb_bnb_logo
            "FIL${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.filecoin_fil_logo
            "ATOM${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.cosmos_atom_logo
            "STX${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.stacks_stx_logo
            "SUI${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.sui_sui_logo
            "MATIC${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.polygon_matic_logo
            "ARB${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.arbitrum_arb_logo
            "INJ${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.injective_inj_logo
            "AVAX${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.avalanche_avax_logo
            "TRX${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.tron_trx_logo
            "RUNE${com.codeskraps.core.client.BinanceClient.BASE_ASSET}" -> R.drawable.thorchain_rune_logo
            else -> 0
        }
    }
}