package com.marcoeckstein.binance.prvt.api.client.account.request

import kotlinx.serialization.Serializable

@Serializable
data class AssetBalanceQuery(
    val needBtcValuation: Boolean
)
