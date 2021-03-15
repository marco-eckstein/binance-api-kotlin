package com.marcoeckstein.binance.prvt.api.client.account

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class IsolatedMarginRebate(
    @[Contextual SerialName("rebateTime")]
    override val timestamp: Instant,
    /**
     * Aka pair
     */
    val symbol: String,
    /**
     * Aka coin
     */
    val asset: String,
    /**
     * Aka amount
     */
    val rebateAmount: Double,
    /**
     * Aka BNB deducted
     */
    val deductBnbAmt: Double,
) : Timestamped
