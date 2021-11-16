@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account.request

import com.marcoeckstein.binance.api.client.prvt.account.PaymentType
import com.marcoeckstein.binance.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

/**
 * A payment history (a.k.a. buy crypto history) query
 *
 * Note that this query would normally implement [PeriodQuery], but [endTime] works in such a way that
 * the developer could not make sense of it. Since the maximum (and default) [pageSize] is quite large (10,000),
 * most users can probably simply use this query without setting [startTime]
 * (which defaults to `1970-01-01T00:00:00Z`) or [endTime] at all.
 */
@Serializable
data class PaymentHistoryQuery(
    @SerialName("page")
    override val pageIndex: Int = 1,
    /**
     * Max: 10,000.
     */
    @SerialName("limit")
    override val pageSize: Int = 10_000,
    val startTime: Instant = Instant.EPOCH,
    /**
     * End time, it is unknown whether it is inclusive or exclusive.
     */
    val endTime: Instant? = null,
    val type: PaymentType,
) : PagingQuery<PaymentHistoryQuery> {

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)
}
