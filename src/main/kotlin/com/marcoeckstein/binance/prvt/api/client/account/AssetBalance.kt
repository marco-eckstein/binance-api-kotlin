@file:UseSerializers(BigDecimalAsPlainStringSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

@Serializable
data class AssetBalance(
    val asset: String,
    val free: BigDecimal,
    val locked: BigDecimal,
    // More properties omitted
)
