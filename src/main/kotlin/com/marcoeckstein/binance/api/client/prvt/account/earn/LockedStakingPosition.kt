@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.api.client.prvt.account.earn

import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

@Serializable
open class LockedStakingPosition(
    val asset: String,
    val amount: BigDecimal,
    /**
     * Aka cumulative interest
     */
    val interest: BigDecimal,
    // More properties omitted
)
