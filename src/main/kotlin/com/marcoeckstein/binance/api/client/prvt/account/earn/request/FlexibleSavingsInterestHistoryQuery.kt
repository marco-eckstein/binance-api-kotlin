@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account.earn.request

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.marcoeckstein.binance.api.client.prvt.account.earn.LendingType
import com.marcoeckstein.binance.api.client.prvt.account.request.PagingQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.PeriodQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.requireValidPeriod
import com.marcoeckstein.binance.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class FlexibleSavingsInterestHistoryQuery(
    override val pageIndex: Int = 1,
    /**
     * Seems to be unlimited.
     */
    override val pageSize: Int? = Int.MAX_VALUE,
    val asset: String? = null,
    val lendingType: LendingType? = null,
    override val startTime: Instant? = null,
    /**
     * End time, inclusive
     */
    override val endTime: Instant? = null,
) : PagingQuery<FlexibleSavingsInterestHistoryQuery>,
    PeriodQuery<FlexibleSavingsInterestHistoryQuery> {

    constructor(
        pageIndex: Int = 1,
        pageSize: Int? = Int.MAX_VALUE,
        asset: String? = null,
        lendingType: LendingType? = null,
        timeRange: Range<Instant>,
    ) : this(
        pageIndex,
        pageSize,
        asset,
        lendingType,
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
