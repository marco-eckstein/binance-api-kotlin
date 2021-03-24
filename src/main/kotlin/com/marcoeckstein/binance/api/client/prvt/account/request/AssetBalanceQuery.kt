package com.marcoeckstein.binance.api.client.prvt.account.request

import kotlinx.serialization.Serializable

@Serializable
data class AssetBalanceQuery(
    val needBtcValuation: Boolean
)
