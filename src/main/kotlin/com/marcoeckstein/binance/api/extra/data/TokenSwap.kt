package com.marcoeckstein.binance.api.extra.data

import java.math.BigDecimal

data class TokenSwap(
    val oldCoin: String,
    val newCoin: String,
    val oldToNewRatio: BigDecimal,
)

val tokenSwaps = setOf(
    // https://www.binance.com/de/support/articles/17595222999545348c8acdd5dc41c00a
    TokenSwap("LEND", "AAVE", BigDecimal("100"))
)
