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
    /**
     * Aka order id
     */
    val orderNo: String,
    val indicatedAmount: BigDecimal,
    /**
     * Aka coin
     */
    val fiatCurrency: String,
    /**
     * E.g. "Successful"
     */
    val statusName: String,
    val amount: BigDecimal,
    val totalFee: BigDecimal,
    /**
     * Aka payment method
     */
    val transactionMethod: String?,
    val createTime: Instant,
    val updateTime: Instant,
    val completedTime: Instant,
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
