@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account.request

import com.marcoeckstein.binance.prvt.api.client.account.WithdrawDirection
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class FiatDepositAndWithdrawHistoryQuery(
    val direction: WithdrawDirection,
    @SerialName("page")
    override val pageIndex: Int,
    /**
     * Max: 2000
     */
    @SerialName("rows")
    override val pageSize: Int,
    val beginTime: Instant? = null,
    val finishTime: Instant? = null,
) : PagingQuery<FiatDepositAndWithdrawHistoryQuery> {

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)
}
