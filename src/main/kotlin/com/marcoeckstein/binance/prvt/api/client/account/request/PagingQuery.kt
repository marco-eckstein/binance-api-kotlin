package com.marcoeckstein.binance.prvt.api.client.account.request

interface PagingQuery<T> {

    val pageIndex: Int
    val pageSize: Int?

    fun forNextPage(): T
}
