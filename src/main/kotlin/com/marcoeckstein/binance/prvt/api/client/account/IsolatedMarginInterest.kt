@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class IsolatedMarginInterest(
    override val txId: String,
    override val chargeEpoch: Instant,
    override val asset: String,
    override val principal: BigDecimal,
    override val interest: BigDecimal,
    override val type: String,
    override val interestRate: BigDecimal,
    override val status: String? = null,
    val base: String,
    val quote: String,
    val symbol: String,
) : PaidInterest
