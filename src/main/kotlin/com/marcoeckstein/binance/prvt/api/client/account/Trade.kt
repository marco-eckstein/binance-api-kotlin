@file:UseSerializers(BigDecimalAsPlainStringSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account

import com.binance.api.client.domain.OrderSide
import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.klib.java.math.equalsComparing
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class Trade(
    val tradeId: Long,
    val price: BigDecimal,
    @[Contextual SerialName("time")]
    override val timestamp: Instant,
    val symbol: String,
    val side: OrderSide,
    val activeBuy: Boolean,
    val qty: BigDecimal,
    val fee: BigDecimal,
    val feeAsset: String,
    val totalQuota: BigDecimal,
    val productName: String?,
    val baseAsset: String,
    val quoteAsset: String,
    val userId: Long,
    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // public val realPnl: 0,
    // public val money: null,
    // public val email: null,
) : Timestamped {

    init {
        require((price * qty) equalsComparing totalQuota.stripTrailingZeros())
    }
}
