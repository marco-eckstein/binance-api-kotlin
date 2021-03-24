@file:UseSerializers(
    InstantAsDateTimeSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.api.client.prvt.account

import com.marcoeckstein.binance.api.client.prvt.InstantAsDateTimeSerializer
import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class Payment(
    @SerialName("createTime")
    override val timestamp: Instant,
    val id: String,
    /**
     * Aka transaction id
     */
    val orderId: String,
    val type: PaymentType,
    /**
     * Found in GUI/.xlsx columns "Price" and "Amount"/"Final Amount".
     */
    val cryptoCurrency: String,
    /**
     * Found in GUI/.xlsx columns "Price" and "Amount"/"Final Amount".
     */
    val fiatCurrency: String,
    val price: BigDecimal,
    /**
     * Aka amount
     */
    val sourceAmount: BigDecimal,
    /**
     * Aka final amount
     */
    val obtainAmount: BigDecimal,
    /**
     * Aka fees
     */
    val tradeFee: BigDecimal,
    val railFee: BigDecimal,
    val totalFee: BigDecimal,
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
    val quoteId: String,
    val userId: String,
    val email: String,
    val enableRecurring: Boolean? = null,
    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // val mode: null,
    // val desc: null,
    // val recurringBuyId: null,
    // val purchaseTypeName: null,
    // val transferSource: null,
    // val transferStatus: null,
    // val errorCode: null,
) : Timestamped
