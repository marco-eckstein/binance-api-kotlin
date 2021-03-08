@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account.request

import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class DistributionHistoryQuery(
    @SerialName("page")
    override val pageIndex: Int = 1,
    /**
     * Max: 2000
     */
    @SerialName("rows")
    override val pageSize: Int = 2000,
    val asset: String? = null,
    override val startTime: Instant? = null,
    /**
     * End time, inclusive
     */
    override val endTime: Instant? = null,
) : PagingQuery<DistributionHistoryQuery>,
    PeriodQuery<DistributionHistoryQuery> {

    init {
        requireValidPeriod()
    }

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)

    override val isEndTimeInclusive get() = true

    override fun copyWith(startTime: Instant?, endTime: Instant?) =
        copy(startTime = startTime, endTime = endTime)
}
