@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.api.client.prvt.account.earn

import com.marcoeckstein.binance.api.client.prvt.account.Timestamped
import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.api.lib.jvm.InstantAsEpochMilliSerializer
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
    val interest: BigDecimal,
    val duration: String,
    val type: String,
) : Timestamped
