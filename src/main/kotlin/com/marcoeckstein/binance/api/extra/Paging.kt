package com.marcoeckstein.binance.api.extra

import com.marcoeckstein.binance.api.client.prvt.account.request.PagingQuery

fun <TQuery : PagingQuery<TQuery>, TResultItem> executeWithPaging(
    queries: Iterable<TQuery>,
    call: (TQuery) -> List<TResultItem>
): List<TResultItem> =
    queries.flatMap { executeWithPaging(it, call) }

fun <TQuery : PagingQuery<TQuery>, TResultItem> executeWithPaging(
    query: TQuery,
    call: (TQuery) -> List<TResultItem>
): List<TResultItem> {
    require(query.pageIndex == 1)
    val pageSize = requireNotNull(query.pageSize)
    var currentQuery = query
    val allResultItems = mutableListOf<TResultItem>()
    do {
        val currentResultItems = call(currentQuery)
        allResultItems.addAll(0, currentResultItems.reversed())
        currentQuery = currentQuery.forNextPage()
    } while (currentResultItems.size == pageSize)
    return allResultItems
}
