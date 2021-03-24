package com.marcoeckstein.binance.api.client.prvt.account.request

data class PagingQueryImpl(
    override val pageIndex: Int = 1,
    override val pageSize: Int,
) : PagingQuery<PagingQueryImpl> {

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)
}
