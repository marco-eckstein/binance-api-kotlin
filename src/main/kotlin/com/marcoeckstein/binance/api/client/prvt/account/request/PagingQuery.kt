package com.marcoeckstein.binance.api.client.prvt.account.request

interface PagingQuery<T> {

    /**
     * One-based page index
     */
    val pageIndex: Int

    val pageSize: Int?

    fun forNextPage(): T
}
