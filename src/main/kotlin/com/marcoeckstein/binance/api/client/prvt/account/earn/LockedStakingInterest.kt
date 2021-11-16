@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    IntAsStringSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.api.client.prvt.account.earn

import com.marcoeckstein.binance.api.client.prvt.account.Timestamped
import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.api.lib.jvm.InstantAsEpochMilliSerializer
import com.marcoeckstein.binance.api.lib.jvm.IntAsStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class LockedStakingInterest(
    @SerialName("createTimestamp")
    override val timestamp: Instant,
    val asset: String,
    val amount: BigDecimal,
    val duration: Int?,
    val type: String,
    // More properties omitted
) : Timestamped
