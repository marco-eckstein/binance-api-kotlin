@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.binance.api.client.domain.OrderSide
import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.equalsComparingTo
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class Trade(
    val tradeId: Long,
    val price: BigDecimal,
    val time: Instant,
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
) {

    init {
        require((price * qty) equalsComparingTo totalQuota.stripTrailingZeros())
    }
}
