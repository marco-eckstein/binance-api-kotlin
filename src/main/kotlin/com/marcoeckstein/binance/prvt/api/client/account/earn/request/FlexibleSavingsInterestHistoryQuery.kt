@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account.earn.request

import com.marcoeckstein.binance.prvt.api.client.account.earn.LendingType
import com.marcoeckstein.binance.prvt.api.client.account.request.PagingQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.PeriodQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.requireValidPeriod
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
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

    init {
        requireValidPeriod()
    }

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)

    override fun copyWith(startTime: Instant?, endTime: Instant?) =
        copy(startTime = startTime, endTime = endTime)

    override val periodInfo get() = Companion

    companion object : PeriodQuery.PeriodInfo {

        override val isEndTimeInclusive = true
        override val timestampResolution = ChronoUnit.MILLIS
    }
}
