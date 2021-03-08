@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import com.marcoeckstein.klib.java.math.equalsComparing
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class IsolatedMarginRepayment(
    override val timestamp: Instant,
    override val txId: String,
    override val asset: String,
    override val amount: BigDecimal,
    override val principal: BigDecimal,
    override val interest: BigDecimal,
    override val status: String,
    val base: String,
    val quote: String,
    val symbol: String,
) : Repayment {

    init {
        require(amount >= BigDecimal.ZERO)
        require(principal >= BigDecimal.ZERO)
        require(interest >= BigDecimal.ZERO)
        require(amount equalsComparing principal + interest)
    }
}
