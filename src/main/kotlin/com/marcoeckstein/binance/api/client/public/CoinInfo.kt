package com.marcoeckstein.binance.api.client.public

import kotlinx.serialization.Serializable

@Serializable
data class CoinInfo(
    val coin: String,
    val isLegalMoney: Boolean,
    // More properties omitted
)
