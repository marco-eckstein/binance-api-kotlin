@file:UseSerializers(BigDecimalAsPlainStringSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account.earn

import com.marcoeckstein.binance.api.client.prvt.account.Timestamped
import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class FlexibleSavingsInterest(
    @[Contextual SerialName("createTimestamp")]
    override val timestamp: Instant,
    val id: String,
    val asset: String,
    val amount: BigDecimal,
    val productName: String,
    val userId: String,
    val lendingType: LendingType,
) : Timestamped
