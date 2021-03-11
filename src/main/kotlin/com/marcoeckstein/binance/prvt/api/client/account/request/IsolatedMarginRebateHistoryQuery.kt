@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account.request

import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class IsolatedMarginRebateHistoryQuery(
    @SerialName("current")
    override val pageIndex: Int = 1,
    /**
     * Seems to be unlimited.
     */
    @SerialName("size")
    override val pageSize: Int? = Int.MAX_VALUE,
    val asset: String? = null,
    val symbol: String? = null,
    override val startTime: Instant? = null,
    /**
     * End time, exclusive
     */
    override val endTime: Instant? = null,
) : PagingQuery<IsolatedMarginRebateHistoryQuery>,
    PeriodQuery<IsolatedMarginRebateHistoryQuery> {

    init {
        requireValidPeriod()
    }

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)

    override fun copyWith(startTime: Instant?, endTime: Instant?) =
        copy(startTime = startTime, endTime = endTime)

    override val periodInfo get() = Companion

    companion object : PeriodQuery.PeriodInfo {

        override val isEndTimeInclusive = false
        override val timestampResolution = ChronoUnit.MILLIS
    }
}
