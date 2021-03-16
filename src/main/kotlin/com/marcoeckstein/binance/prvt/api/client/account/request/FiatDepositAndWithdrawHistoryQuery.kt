@file:UseSerializers(InstantAsEpochSecondSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account.request

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.marcoeckstein.binance.prvt.api.client.account.WithdrawDirection
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochSecondSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class FiatDepositAndWithdrawHistoryQuery(
    val direction: WithdrawDirection,
    @SerialName("page")
    override val pageIndex: Int = 1,
    /**
     * Max: 2000
     */
    @SerialName("rows")
    override val pageSize: Int = 2000,
    @SerialName("beginTime")
    override val startTime: Instant? = null,
    /**
     * End time, inclusive
     */
    @SerialName("finishTime")
    override val endTime: Instant? = null,
) : PagingQuery<FiatDepositAndWithdrawHistoryQuery>,
    PeriodQuery<FiatDepositAndWithdrawHistoryQuery> {

    constructor(
        direction: WithdrawDirection,
        pageIndex: Int = 1,
        pageSize: Int = 2000,
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
        override val timestampResolution = ChronoUnit.SECONDS
    }
}
