@file:UseSerializers(BigDecimalAsPlainStringSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account

import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class CrossMarginPaidInterest(
    @[Contextual SerialName("chargeEpoch")]
    override val timestamp: Instant,
    override val txId: String,
    override val asset: String,
    override val principal: BigDecimal,
    override val interest: BigDecimal,
    override val type: String,
    override val interestRate: BigDecimal,
) : PaidInterest
