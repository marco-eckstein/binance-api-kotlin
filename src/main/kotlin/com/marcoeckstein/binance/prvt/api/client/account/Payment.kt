@file:UseSerializers(
    InstantAsDateSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsDateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class Payment(
    val id: String,
    val type: PaymentType,
    /**
     * Aka amount
     */
    val sourceAmount: BigDecimal,
    /**
     * Aka fees
     */
    val totalFee: BigDecimal,
    val tradeFee: BigDecimal,
    val railFee: BigDecimal,
    val price: BigDecimal,
    /**
     * Aka final amount
     */
    val obtainAmount: BigDecimal,
    /**
     * "4" = "Completed"
     * "5" = "Failed"
     */
    val status: String,
    /**
     * Aka method
     *
     * "0" = "Cash Balance"
     */
    val payType: String,
    val rail: String,
    /**
     * Aka date
     */
    val createTime: Instant,
    /**
     * Aka transaction id
     */
    val orderId: String,
    val quoteId: String,
    val userId: String,
    val email: String,
    /**
     * Found in GUI/.xlsx columns "Price" and "Amount"/"Final Amount".
     */
    val cryptoCurrency: String,
    /**
     * Found in GUI/.xlsx columns "Price" and "Amount"/"Final Amount".
     */
    val fiatCurrency: String,
    val enableRecurring: Boolean? = null,
    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // val mode: null,
    // val desc: null,
    // val recurringBuyId: null,
    // val purchaseTypeName: null,
    // val transferSource: null,
    // val transferStatus: null,
    // val errorCode: null,
)
