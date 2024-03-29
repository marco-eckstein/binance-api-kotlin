@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account.request

import com.binance.api.client.domain.OrderSide
import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.marcoeckstein.binance.api.client.prvt.account.AccountType
import com.marcoeckstein.binance.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class OrderHistoryQuery(
    @SerialName("page")
    override val pageIndex: Int = 1,
    /**
     * Max: 2000
     */
    @SerialName("rows")
    override val pageSize: Int = 2000,
    val accountType: AccountType,
    override val startTime: Instant? = null,
    /**
     * End time, exclusive
     */
    override val endTime: Instant? = null,
    val baseAsset: String? = null,
    val quoteAsset: String? = null,
    /**
     * An alternative to using baseAsset and/or quoteAsset.
     */
    val symbol: String? = null,
    val direction: OrderSide? = null,
    val hideCancel: Boolean,
) : PagingQuery<OrderHistoryQuery>, PeriodQuery<OrderHistoryQuery> {

    constructor(
        pageIndex: Int = 1,
        pageSize: Int = 2000,
        accountType: AccountType,
        timeRange: Range<Instant>,
        baseAsset: String? = null,
        quoteAsset: String? = null,
        symbol: String? = null,
        direction: OrderSide? = null,
        hideCancel: Boolean,
    ) : this(
        pageIndex,
        pageSize,
        accountType,
        startTime = calculateStartTime(timeRange),
        endTime = calculateEndTime(timeRange),
        baseAsset,
        quoteAsset,
        symbol,
        direction,
        hideCancel,
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
