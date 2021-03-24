@file:UseSerializers(BigDecimalAsPlainStringSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account

import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class CrossMarginBorrowing(
    @Contextual
    override val timestamp: Instant,
    override val txId: String,
    override val asset: String,
    override val principal: BigDecimal,
    override val status: String,
    override val fromId: String? = null,
    override val toId: String? = null,
) : Borrowing
