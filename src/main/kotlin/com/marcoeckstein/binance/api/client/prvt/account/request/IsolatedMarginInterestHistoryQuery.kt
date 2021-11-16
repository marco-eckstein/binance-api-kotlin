@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account.request

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.marcoeckstein.binance.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class IsolatedMarginInterestHistoryQuery(
    @SerialName("current")
    override val pageIndex: Int = 1,
    /**
     * Max: 100 (Seems to be unlimited sometimes.)
     */
    @SerialName("size")
    override val pageSize: Int? = 100,
    override val startTime: Instant? = null,
    /**
     * End time, inclusive
     */
    override val endTime: Instant? = null,
    val archived: Boolean? = true,
) : PagingQuery<IsolatedMarginInterestHistoryQuery>, PeriodQuery<IsolatedMarginInterestHistoryQuery> {

    constructor(
        pageIndex: Int = 1,
        pageSize: Int? = 100,
        timeRange: Range<Instant>,
    ) : this(
        pageIndex = pageIndex,
        pageSize = pageSize,
        startTime = calculateStartTime(timeRange),
        endTime = calculateEndTime(timeRange),
    )

    init {
        requireValidPeriod()
    }

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)

    override fun copyWith(startTime: Instant?, endTime: Instant?) =
        copy(startTime = startTime, endTime = endTime)

    override val periodInfo get() = Companion

    companion object : PeriodQuery.PeriodInfo {

        override val endTimeType = BoundType.CLOSED
        override val timestampResolution = ChronoUnit.SECONDS
    }
}
