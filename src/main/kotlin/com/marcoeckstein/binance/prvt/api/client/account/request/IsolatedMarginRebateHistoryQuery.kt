@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account.request

import com.google.common.collect.BoundType
import com.google.common.collect.Range
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

    constructor(
        pageIndex: Int = 1,
        pageSize: Int? = Int.MAX_VALUE,
        asset: String? = null,
        symbol: String? = null,
        timeRange: Range<Instant>,
    ) : this(
        pageIndex = pageIndex,
        pageSize = pageSize,
        asset = asset,
        symbol = symbol,
        startTime = calculateEndTime(timeRange),
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

        override val endTimeType = BoundType.OPEN
        override val timestampResolution = ChronoUnit.MILLIS
    }
}
