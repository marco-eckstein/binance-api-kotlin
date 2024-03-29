@file:UseSerializers(InstantAsIsoDateTimeSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account.request

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.marcoeckstein.binance.api.client.prvt.account.WithdrawDirection
import com.marcoeckstein.binance.api.lib.jvm.InstantAsIsoDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class FiatDepositAndWithdrawHistoryQuery(
    val businessType: WithdrawDirection,
    @SerialName("page")
    override val pageIndex: Int = 1,
    /**
     * Seems to be unlimited
     */
    @SerialName("rows")
    override val pageSize: Int = 2000,
    @SerialName("startDate")
    override val startTime: Instant? = null,
    /**
     * End time, inclusive
     */
    @SerialName("endDate")
    override val endTime: Instant? = null,
) : PagingQuery<FiatDepositAndWithdrawHistoryQuery>,
    PeriodQuery<FiatDepositAndWithdrawHistoryQuery> {

    constructor(
        direction: WithdrawDirection,
        pageIndex: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        timeRange: Range<Instant>,
    ) : this(
        direction,
        pageIndex = pageIndex,
        pageSize = pageSize,
        startTime = calculateStartTime(timeRange),
        endTime = calculateEndTime(timeRange)
    )

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)

    override fun copyWith(startTime: Instant?, endTime: Instant?) =
        copy(startTime = startTime, endTime = endTime)

    override val periodInfo get() = Companion

    companion object : PeriodQuery.PeriodInfo {

        override val endTimeType = BoundType.CLOSED

        /**
         * ChronoUnit.MILLIS seems to be ok, but the result items have only a resolution of ChronoUnit.SECONDS,
         * so let's keep that consistent.
         */
        override val timestampResolution = ChronoUnit.SECONDS
    }
}
