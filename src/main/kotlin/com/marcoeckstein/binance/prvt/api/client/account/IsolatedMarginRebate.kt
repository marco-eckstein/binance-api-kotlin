@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class IsolatedMarginRebate(
    @SerialName("rebateTime")
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
