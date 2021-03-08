package com.marcoeckstein.binance.prvt.api.client.account.request

import com.marcoeckstein.binance.prvt.api.client.account.PaymentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentHistoryQuery(
    @SerialName("page")
    override val pageIndex: Int = 1,
    /**
     * Seems to be unlimited.
     */
    @SerialName("limit")
    override val pageSize: Int = Int.MAX_VALUE,
    val type: PaymentType,
) : PagingQuery<PaymentHistoryQuery> {

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)
}
