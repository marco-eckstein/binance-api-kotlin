package com.marcoeckstein.binance.prvt.api.extra.extensions

import com.marcoeckstein.binance.prvt.api.client.BinancePrivateApiRestClient
import com.marcoeckstein.binance.prvt.api.client.account.AccountType
import com.marcoeckstein.binance.prvt.api.client.account.AssetBalance
import com.marcoeckstein.binance.prvt.api.client.account.Distribution
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginBorrowing
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginInterest
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginRebate
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginRepayment
import com.marcoeckstein.binance.prvt.api.client.account.Order
import com.marcoeckstein.binance.prvt.api.client.account.PaidInterest
import com.marcoeckstein.binance.prvt.api.client.account.Payment
import com.marcoeckstein.binance.prvt.api.client.account.PaymentType
import com.marcoeckstein.binance.prvt.api.client.account.Trade
import com.marcoeckstein.binance.prvt.api.client.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.prvt.api.client.account.earn.FlexibleSavingsPosition
import com.marcoeckstein.binance.prvt.api.client.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.prvt.api.client.account.earn.LockedStakingPosition
import com.marcoeckstein.binance.prvt.api.client.account.earn.request.FlexibleSavingsInterestHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.earn.request.LockedStakingInterestHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.AssetBalanceQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.DistributionHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.HistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.IsolatedMarginRebateHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.OpenOrdersQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.OrderHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.PagingQueryImpl
import com.marcoeckstein.binance.prvt.api.client.account.request.PaymentHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.TradeHistoryQuery
import com.marcoeckstein.binance.prvt.api.extra.executeWithPaging
import java.time.Instant
import java.time.temporal.ChronoUnit

fun BinancePrivateApiRestClient.getPaymentHistory(paymentType: PaymentType): List<Payment> =
    executeWithPaging(PaymentHistoryQuery(pageIndex = 1, pageSize = 100, type = paymentType)) {
        getPaymentHistory(it)
    }

fun BinancePrivateApiRestClient.getPaymentHistory(): List<Payment> =
    getPaymentHistory(PaymentType.BUY) + getPaymentHistory(PaymentType.SELL)

fun BinancePrivateApiRestClient.getSpotAccountBalances(): List<AssetBalance> =
    getSpotAccountBalances(AssetBalanceQuery(needBtcValuation = false))

fun BinancePrivateApiRestClient.getOpenOrders(queries: Iterable<OpenOrdersQuery>): List<Order> =
    queries.flatMap { getOpenOrders(it) }

fun BinancePrivateApiRestClient.getOpenOrders(): List<Order> {
    val query = OpenOrdersQuery(AccountType.SPOT)
    val queries = listOf(
        query,
        query.copy(accountType = AccountType.CROSS_MARGIN),
        query.copy(accountType = AccountType.ISOLATED_MARGIN)
    )
    return getOpenOrders(queries)
}

fun BinancePrivateApiRestClient.getOrderHistory(queries: Iterable<OrderHistoryQuery>): List<Order> =
    queries.flatMap { getOrderHistory(it) }

fun BinancePrivateApiRestClient.getTradeHistory(queries: Iterable<TradeHistoryQuery>): List<Trade> =
    queries.flatMap { getTradeHistory(it) }

fun BinancePrivateApiRestClient.getTradeHistory(startTime: Instant): List<Trade> {
    val query = TradeHistoryQuery(
        pageIndex = 1,
        pageSize = 2000,
        accountType = AccountType.SPOT,
        startTime = startTime,
        endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    )
    val queries = listOf(
        query,
        query.copy(accountType = AccountType.CROSS_MARGIN),
        query.copy(accountType = AccountType.ISOLATED_MARGIN)
    ).flatMap { it.splitPeriod() }
    return executeWithPaging(queries) { getTradeHistory(it) }
}

fun BinancePrivateApiRestClient.getDistributionHistory(
    queries: Iterable<DistributionHistoryQuery>
): List<Distribution> =
    queries.flatMap { getDistributionHistory(it) }

fun BinancePrivateApiRestClient.getDistributionHistory(startTime: Instant): List<Distribution> {
    val query = DistributionHistoryQuery(
        pageIndex = 1,
        pageSize = 2000,
        startTime = startTime,
        endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    )
    return executeWithPaging(query.splitPeriod()) { getDistributionHistory(it) }
}

fun BinancePrivateApiRestClient.getFlexibleSavingsPositions(): List<FlexibleSavingsPosition> =
    executeWithPaging(PagingQueryImpl(pageIndex = 1, pageSize = 50)) {
        getFlexibleSavingsPositions(pageIndex = it.pageIndex, pageSize = it.pageSize!!)
    }

fun BinancePrivateApiRestClient.getFlexibleSavingsInterestHistory(
    queries: Iterable<FlexibleSavingsInterestHistoryQuery>
): List<FlexibleSavingsInterest> =
    queries.flatMap { getFlexibleSavingsInterestHistory(it) }

fun BinancePrivateApiRestClient.getFlexibleSavingsInterestHistory(
    startTime: Instant
): List<FlexibleSavingsInterest> {
    val query = FlexibleSavingsInterestHistoryQuery(
        pageIndex = 1,
        pageSize = Integer.MAX_VALUE,
        startTime = startTime,
        endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    )
    return executeWithPaging(query.splitPeriod()) { getFlexibleSavingsInterestHistory(it) }
}

fun BinancePrivateApiRestClient.getLockedStakingPositions(): List<LockedStakingPosition> =
    executeWithPaging(PagingQueryImpl(pageIndex = 1, pageSize = 200)) {
        getLockedStakingPositions(pageIndex = it.pageIndex, pageSize = it.pageSize!!)
    }

fun BinancePrivateApiRestClient.getLockedStakingInterestHistory(
    queries: Iterable<LockedStakingInterestHistoryQuery>
): List<LockedStakingInterest> =
    queries.flatMap { getLockedStakingInterestHistory(it) }

fun BinancePrivateApiRestClient.getLockedStakingInterestHistory(
    startTime: Instant
): List<LockedStakingInterest> {
    val query = LockedStakingInterestHistoryQuery(
        pageIndex = 1,
        pageSize = 100,
        startTime = startTime,
        endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    )
    return executeWithPaging(query.splitPeriod()) { getLockedStakingInterestHistory(it) }
}

fun BinancePrivateApiRestClient.getIsolatedMarginBorrowingHistory(
    queries: Iterable<HistoryQuery>
): List<IsolatedMarginBorrowing> =
    queries.flatMap { getIsolatedMarginBorrowingHistory(it) }

fun BinancePrivateApiRestClient.getIsolatedMarginBorrowingHistory(
    startTime: Instant
): List<IsolatedMarginBorrowing> {
    val query = HistoryQuery(
        pageIndex = 1,
        pageSize = 2000,
        startTime = startTime,
        endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    )
    return executeWithPaging(query.splitPeriod()) { getIsolatedMarginBorrowingHistory(it) }
}

fun BinancePrivateApiRestClient.getIsolatedMarginRepaymentHistory(
    queries: Iterable<HistoryQuery>
): List<IsolatedMarginRepayment> =
    queries.flatMap { getIsolatedMarginRepaymentHistory(it) }

fun BinancePrivateApiRestClient.getIsolatedMarginRepaymentHistory(
    startTime: Instant
): List<IsolatedMarginRepayment> {
    val query = HistoryQuery(
        pageIndex = 1,
        pageSize = 2000,
        startTime = startTime,
        endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    )
    return executeWithPaging(query.splitPeriod()) { getIsolatedMarginRepaymentHistory(it) }
}

fun BinancePrivateApiRestClient.getIsolatedMarginInterestHistory(
    queries: Iterable<HistoryQuery>
): List<PaidInterest> =
    queries.flatMap { getIsolatedMarginInterestHistory(it) }

fun BinancePrivateApiRestClient.getIsolatedMarginInterestHistory(
    startTime: Instant
): List<IsolatedMarginInterest> {
    val query = HistoryQuery(
        pageIndex = 1,
        pageSize = 50000,
        startTime = startTime,
        endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    )
    return executeWithPaging(query.splitPeriod()) { getIsolatedMarginInterestHistory(it) }
        .distinctBy { it.txId }
}

fun BinancePrivateApiRestClient.getIsolatedMarginRebateHistory(
    queries: Iterable<IsolatedMarginRebateHistoryQuery>
): List<IsolatedMarginRebate> =
    queries.flatMap { getIsolatedMarginRebateHistory(it) }

fun BinancePrivateApiRestClient.getIsolatedMarginRebateHistory(startTime: Instant): List<IsolatedMarginRebate> {
    val query = IsolatedMarginRebateHistoryQuery(
        pageIndex = 1,
        pageSize = Int.MAX_VALUE,
        startTime = startTime,
        endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    )
    return executeWithPaging(query.splitPeriod()) { getIsolatedMarginRebateHistory(it) }
}
