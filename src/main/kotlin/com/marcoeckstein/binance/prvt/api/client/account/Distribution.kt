@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class Distribution(
    @SerialName("operateTime")
    override val timestamp: Instant,
    val amount: BigDecimal,
    val asset: String,
    val info: String,
) : Timestamped
