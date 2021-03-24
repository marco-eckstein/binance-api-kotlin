@file:UseSerializers(
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.api.client.prvt.account

import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

@Serializable
data class IsolatedMarginAccountPosition(
    val base: Asset,
    val quote: Asset,
    val liquidatePrice: BigDecimal?,
    val liquidateRate: BigDecimal?,
    // More properties omitted
) {

    @Serializable
    data class Asset(
        val assetName: String,
        val position: BigDecimal?,
        // More properties omitted
    )
}
