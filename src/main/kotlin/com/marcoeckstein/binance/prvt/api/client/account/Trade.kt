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
    @[Contextual SerialName("time")]
    override val timestamp: Instant,
    val tradeId: Long,
    val side: OrderSide,
    val symbol: String,
    val baseAsset: String,
    val quoteAsset: String,
    val price: BigDecimal,
    val qty: BigDecimal,
    val fee: BigDecimal,
    val feeAsset: String,
    val totalQuota: BigDecimal,
    val activeBuy: Boolean,
    val userId: Long,
    val productName: String?,
    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // public val realPnl: 0,
    // public val money: null,
    // public val email: null,
) : Timestamped {

    init {
        require((price * qty) equalsComparing totalQuota.stripTrailingZeros())
    }
}
