@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account.request

import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class HistoryQuery(
    @SerialName("current")
    override val pageIndex: Int,
    @SerialName("size")
    override val pageSize: Int? = null,
    override val startTime: Instant? = null,
    override val endTime: Instant? = null,
) : PagingQuery<HistoryQuery>, PeriodQuery<HistoryQuery> {

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)

    override fun copyWith(startTime: Instant?, endTime: Instant?) =
        copy(startTime = startTime, endTime = endTime)
}
