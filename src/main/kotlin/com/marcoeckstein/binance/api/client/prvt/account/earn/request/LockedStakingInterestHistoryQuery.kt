@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account.earn.request

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.marcoeckstein.binance.api.client.prvt.account.request.PagingQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.PeriodQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.requireValidPeriod
import com.marcoeckstein.binance.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class LockedStakingInterestHistoryQuery(
    override val pageIndex: Int = 1,
    /**
     * Max: 100
     */
    override val pageSize: Int? = 100,
    val asset: String? = null,
    override val startTime: Instant? = null,
    /**
     * End time, inclusive
     */
    override val endTime: Instant? = null,
) : PagingQuery<LockedStakingInterestHistoryQuery>,
    PeriodQuery<LockedStakingInterestHistoryQuery> {

    constructor(
        pageIndex: Int = 1,
        pageSize: Int? = 100,
        asset: String? = null,
        timeRange: Range<Instant>,
    ) : this(
        pageIndex,
        pageSize,
        asset,
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
        override val timestampResolution = ChronoUnit.MILLIS
    }
}
