@file:UseSerializers(BigDecimalAsPlainStringSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochSecondSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class FiatDepositAndWithdrawHistoryEntry(
    val txId: Long,
    @[Serializable(with = InstantAsEpochSecondSerializer::class) SerialName("applyTime")]
    override val timestamp: Instant,
    val coin: String,
    /**
     * Aka order id
     */
    val paymentId: String,
    val statusName: String,
    val transactionFee: BigDecimal,
    /**
     * Aka indicated amount
     */
    val transferAmount: BigDecimal,
    /**
     * Aka amount
     */
    val expectedAmount: BigDecimal?,
    val paymentMethod: String?,
    // More properties omitted
) : Timestamped {

//    init {
//        expectedAmount?.let {
//            val calculatedExpectedAmount: BigDecimal = transferAmount - transactionFee
//            require(it == calculatedExpectedAmount) {
//                "Expected amount == $it !== $calculatedExpectedAmount == calculated expected amount"
//            }
//        }
//    }
}
