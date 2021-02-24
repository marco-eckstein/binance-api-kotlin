@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account.earn.request

import com.marcoeckstein.binance.prvt.api.client.account.earn.LendingType
import com.marcoeckstein.binance.prvt.api.client.account.request.PagingQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.PeriodQuery
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class FlexibleSavingsInterestHistoryQuery(
    override val pageIndex: Int,
    /**
     * Seems to be unlimited.
     */
    override val pageSize: Int? = null,
    val asset: String? = null,
    val lendingType: LendingType? = null,
    override val startTime: Instant? = null,
    override val endTime: Instant? = null,
) : PagingQuery<FlexibleSavingsInterestHistoryQuery>,
    PeriodQuery<FlexibleSavingsInterestHistoryQuery> {

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)

    override fun copyWith(startTime: Instant?, endTime: Instant?) =
        copy(startTime = startTime, endTime = endTime)
}
