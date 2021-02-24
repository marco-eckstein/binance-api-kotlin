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
data class CrossMarginBorrowing(
    override val timestamp: Instant,
    override val txId: String,
    override val asset: String,
    override val principal: BigDecimal,
    override val status: String,
    override val fromId: String? = null,
    override val toId: String? = null,
) : Borrowing
