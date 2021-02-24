package com.marcoeckstein.binance.prvt.api.client.account.request

import com.marcoeckstein.binance.prvt.api.client.account.PaymentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentHistoryQuery(
    /**
     * One-based page index
     */
    @SerialName("page")
    override val pageIndex: Int,
    @SerialName("limit")
    override val pageSize: Int,
    val type: PaymentType,
) : PagingQuery<PaymentHistoryQuery> {

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)
}
