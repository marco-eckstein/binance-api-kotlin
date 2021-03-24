@file:UseSerializers(BigDecimalAsPlainStringSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account

import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class CrossMarginTransfer(
    @Contextual
    override val timestamp: Instant,
    override val txId: String,
    override val uid: String,
    override val asset: String,
    override val amount: BigDecimal,
    override val status: String,
    override val type: RollDirection,
    override val target: String,
) : Transfer
