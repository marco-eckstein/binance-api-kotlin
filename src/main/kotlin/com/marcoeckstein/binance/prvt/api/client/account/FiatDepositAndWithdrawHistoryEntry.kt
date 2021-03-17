@file:UseSerializers(
    BigDecimalAsPlainStringSerializer::class,
    InstantAsDateTimeSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.client.InstantAsDateTimeSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class FiatDepositAndWithdrawHistoryEntry(
    val createTime: Instant,
    val updateTime: Instant,
    val completedTime: Instant,
    /**
     * Aka order id
     */
    val orderNo: String,
    /**
     * Aka coin
     */
    val fiatCurrency: String,
    val amount: BigDecimal,
    val totalFee: BigDecimal,
    val indicatedAmount: BigDecimal,
    /**
     * Aka payment method
     */
    val transactionMethod: String?,
    /**
     * E.g. "Successful"
     */
    val statusName: String,
    // More properties omitted
) : Timestamped {

    init {
        val calculatedAmount = indicatedAmount - totalFee
        require(amount == calculatedAmount) { "Expected amount == $calculatedAmount, but was $amount." }
        require(createTime <= completedTime)
        require(completedTime <= updateTime)
    }

    override val timestamp: Instant get() = createTime
}
