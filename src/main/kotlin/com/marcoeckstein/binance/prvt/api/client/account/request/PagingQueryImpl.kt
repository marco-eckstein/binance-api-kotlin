package com.marcoeckstein.binance.prvt.api.client.account.request

data class PagingQueryImpl(
    override val pageIndex: Int,
    override val pageSize: Int?,
) : PagingQuery<PagingQueryImpl> {

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)
}
