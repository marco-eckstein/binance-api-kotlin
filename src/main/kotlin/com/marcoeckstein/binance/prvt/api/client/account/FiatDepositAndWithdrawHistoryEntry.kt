@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class FiatDepositAndWithdrawHistoryEntry(
    val txId: Long,
    /**
     * Aka date
     */
    val applyTime: Instant,
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
) {

    val calculatedExpectedAmount: BigDecimal =
        transferAmount - transactionFee

    init {
        expectedAmount?.let {
            require(it == calculatedExpectedAmount) {
                "Expected amount == $it !== calculated expected amount == $calculatedExpectedAmount"
            }
        }
    }
}
