package com.codeskraps.core.domain.util

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.model.Asset

object StateUtil {
    fun logo(asset: Asset): Int {
        return logo("${asset.asset}${BinanceClient.BASE_ASSET}")
    }

    fun logo(symbol: String): Int {
        return when (symbol) {
            "SOL${BinanceClient.BASE_ASSET}" -> R.drawable.solana_sol_logo
            "BTC${BinanceClient.BASE_ASSET}" -> R.drawable.bitcoin_btc_logo
            "ETH${BinanceClient.BASE_ASSET}" -> R.drawable.ethereum_eth_logo
            "LINK${BinanceClient.BASE_ASSET}" -> R.drawable.chainlink_link_logo
            "ADA${BinanceClient.BASE_ASSET}" -> R.drawable.cardano_ada_logo
            "USDT${BinanceClient.BASE_ASSET}" -> R.drawable.tether_usdt_logo
            "BNB${BinanceClient.BASE_ASSET}" -> R.drawable.bnb_bnb_logo
            "FIL${BinanceClient.BASE_ASSET}" -> R.drawable.filecoin_fil_logo
            "ATOM${BinanceClient.BASE_ASSET}" -> R.drawable.cosmos_atom_logo
            "STX${BinanceClient.BASE_ASSET}" -> R.drawable.stacks_stx_logo
            "SUI${BinanceClient.BASE_ASSET}" -> R.drawable.sui_sui_logo
            "MATIC${BinanceClient.BASE_ASSET}" -> R.drawable.polygon_matic_logo
            "ARB${BinanceClient.BASE_ASSET}" -> R.drawable.arbitrum_arb_logo
            "INJ${BinanceClient.BASE_ASSET}" -> R.drawable.injective_inj_logo
            "AVAX${BinanceClient.BASE_ASSET}" -> R.drawable.avalanche_avax_logo
            "TRX${BinanceClient.BASE_ASSET}" -> R.drawable.tron_trx_logo
            "RUNE${BinanceClient.BASE_ASSET}" -> R.drawable.thorchain_rune_logo
            "RNDR${BinanceClient.BASE_ASSET}" -> R.drawable.render_token_logo
            "MANTA${BinanceClient.BASE_ASSET}" -> R.drawable.manta_network_logo
            "BONK${BinanceClient.BASE_ASSET}" -> R.drawable.bonk_logo
            "AR${BinanceClient.BASE_ASSET}" -> R.drawable.arweave_ar_logo
            "DOT${BinanceClient.BASE_ASSET}" -> R.drawable.polkadot_new_dot_logo
            "FTM${BinanceClient.BASE_ASSET}" -> R.drawable.fantom_ftm_logo
            "VET${BinanceClient.BASE_ASSET}" -> R.drawable.vechain_vet_logo
            "ALGO${BinanceClient.BASE_ASSET}" -> R.drawable.algorand_algo_logo
            "SUPER${BinanceClient.BASE_ASSET}" -> R.drawable.super_farm_logo
            "PYR${BinanceClient.BASE_ASSET}" -> R.drawable.vulkan_forged_pyr_logo
            else -> 0
        }
    }
}