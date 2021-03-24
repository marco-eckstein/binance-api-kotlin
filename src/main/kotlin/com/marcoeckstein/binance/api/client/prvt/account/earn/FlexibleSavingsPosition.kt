@file:UseSerializers(
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.api.client.prvt.account.earn

import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

@Serializable
data class FlexibleSavingsPosition(
    val asset: String,
    val totalAmount: BigDecimal,
    // More properties omitted
)
