@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account.earn

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class FlexibleSavingsInterest(
    val id: String,
    val createTimestamp: Instant,
    val productName: String,
    val userId: String,
    val asset: String,
    val amount: BigDecimal,
    val lendingType: LendingType,
)
