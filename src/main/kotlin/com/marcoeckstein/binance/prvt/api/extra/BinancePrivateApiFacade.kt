package com.marcoeckstein.binance.prvt.api.extra

import com.google.common.collect.Range
import com.marcoeckstein.binance.prvt.api.client.BinancePrivateApiRestClient
import com.marcoeckstein.binance.prvt.api.client.account.AccountType
import com.marcoeckstein.binance.prvt.api.client.account.AssetBalance
import com.marcoeckstein.binance.prvt.api.client.account.Distribution
import com.marcoeckstein.binance.prvt.api.client.account.FiatDepositAndWithdrawHistoryEntry
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginAccountDetail
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginAccountPosition
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginBorrowing
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginInterest
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginRebate
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginRepayment
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginTransfer
import com.marcoeckstein.binance.prvt.api.client.account.Order
import com.marcoeckstein.binance.prvt.api.client.account.Payment
import com.marcoeckstein.binance.prvt.api.client.account.PaymentType
import com.marcoeckstein.binance.prvt.api.client.account.Trade
import com.marcoeckstein.binance.prvt.api.client.account.WithdrawDirection
import com.marcoeckstein.binance.prvt.api.client.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.prvt.api.client.account.earn.FlexibleSavingsPosition
import com.marcoeckstein.binance.prvt.api.client.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.prvt.api.client.account.earn.LockedStakingPosition
import com.marcoeckstein.binance.prvt.api.client.account.earn.request.FlexibleSavingsInterestHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.earn.request.LockedStakingInterestHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.AssetBalanceQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.DistributionHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.FiatDepositAndWithdrawHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.HistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.IsolatedMarginInterestHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.IsolatedMarginRebateHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.OpenOrdersQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.OrderHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.PagingQueryImpl
import com.marcoeckstein.binance.prvt.api.client.account.request.PaymentHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.TradeHistoryQuery
import java.time.Instant
import java.time.temporal.ChronoUnit

@Suppress("TooManyFunctions")
class BinancePrivateApiFacade(
    private val client: BinancePrivateApiRestClient,
    private val defaultStartTime: Instant,
) {

    fun getSpotAccountBalances(): List<AssetBalance> =
        client.getSpotAccountBalances(AssetBalanceQuery(needBtcValuation = false))

    fun getIsolatedMarginAccountDetails(): List<IsolatedMarginAccountDetail> =
        client.getIsolatedMarginAccountDetails()

    fun getIsolatedMarginAccountPositions(): List<IsolatedMarginAccountPosition> =
        client.getIsolatedMarginAccountPositions()

    fun getOpenOrders(accountType: AccountType): List<Order> =
        client.getOpenOrders(accountType)

    fun getFlexibleSavingsPositions(): List<FlexibleSavingsPosition> =
        executeWithPaging(PagingQueryImpl(pageSize = 50)) {
            client.getFlexibleSavingsPositions(pageIndex = it.pageIndex, pageSize = it.pageSize)
        }

    fun getLockedStakingPositions(): List<LockedStakingPosition> =
        executeWithPaging(PagingQueryImpl(pageSize = 200)) {
            client.getLockedStakingPositions(pageIndex = it.pageIndex, pageSize = it.pageSize)
        }

    fun getOpenOrders(query: OpenOrdersQuery): List<Order> =
        client.getOpenOrders(query)

    fun getFiatDepositAndWithdrawHistory(
        direction: WithdrawDirection
    ): List<FiatDepositAndWithdrawHistoryEntry> =
        getFiatDepositAndWithdrawHistory(direction, defaultTimeRange)

    fun getFiatDepositAndWithdrawHistory(
        direction: WithdrawDirection,
        timeRange: Range<Instant>
    ): List<FiatDepositAndWithdrawHistoryEntry> {
        val query = FiatDepositAndWithdrawHistoryQuery(direction, timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), client::getFiatDepositAndWithdrawHistory)
    }

    fun getFiatDepositAndWithdrawHistory(
        query: FiatDepositAndWithdrawHistoryQuery
    ): List<FiatDepositAndWithdrawHistoryEntry> =
        executeWithPaging(query, client::getFiatDepositAndWithdrawHistory)

    fun getPaymentHistory(): List<Payment> =
        getPaymentHistory(PaymentType.BUY) + getPaymentHistory(PaymentType.SELL)

    fun getPaymentHistory(paymentType: PaymentType): List<Payment> =
        getPaymentHistory(PaymentHistoryQuery(type = paymentType))

    fun getPaymentHistory(query: PaymentHistoryQuery): List<Payment> =
        executeWithPaging(query, client::getPaymentHistory)

    fun getOpenOrders(): List<Order> {
        val query = OpenOrdersQuery(AccountType.SPOT)
        val queries = listOf(
            query,
            query.copy(accountType = AccountType.CROSS_MARGIN),
            query.copy(accountType = AccountType.ISOLATED_MARGIN)
        )
        return queries.flatMap(client::getOpenOrders) // no paging
    }

    fun getOrderHistory(
        vararg accountTypes: AccountType =
            arrayOf(AccountType.SPOT, AccountType.CROSS_MARGIN, AccountType.ISOLATED_MARGIN)
    ): List<Order> =
        getOrderHistory(defaultTimeRange, *accountTypes)

    fun getOrderHistory(
        timeRange: Range<Instant>,
        vararg accountTypes: AccountType =
            arrayOf(AccountType.SPOT, AccountType.CROSS_MARGIN, AccountType.ISOLATED_MARGIN)
    ): List<Order> {
        val query = OrderHistoryQuery(
            accountType = AccountType.SPOT,
            timeRange = timeRange,
            hideCancel = false
        )
        val queries = accountTypes.map { query.copy(accountType = it) }.flatMap { it.splitPeriod() }
        return executeWithPaging(queries, client::getOrderHistory)
    }

    fun getOrderHistory(query: OrderHistoryQuery): List<Order> =
        executeWithPaging(query, client::getOrderHistory)

    fun getTradeHistory(
        vararg accountTypes: AccountType =
            arrayOf(AccountType.SPOT, AccountType.CROSS_MARGIN, AccountType.ISOLATED_MARGIN)
    ): List<Trade> =
        getTradeHistory(defaultTimeRange, *accountTypes)

    fun getTradeHistory(
        timeRange: Range<Instant>,
        vararg accountTypes: AccountType =
            arrayOf(AccountType.SPOT, AccountType.CROSS_MARGIN, AccountType.ISOLATED_MARGIN)
    ): List<Trade> {
        val query = TradeHistoryQuery(
            accountType = AccountType.SPOT,
            timeRange = timeRange
        )
        val queries = accountTypes.map { query.copy(accountType = it) }.flatMap { it.splitPeriod() }
        return executeWithPaging(queries, client::getTradeHistory)
    }

    fun getTradeHistory(query: TradeHistoryQuery): List<Trade> =
        executeWithPaging(query, client::getTradeHistory)

    fun getDistributionHistory(): List<Distribution> =
        getDistributionHistory(defaultTimeRange)

    fun getDistributionHistory(timeRange: Range<Instant>): List<Distribution> {
        val query = DistributionHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), client::getDistributionHistory)
    }

    fun getDistributionHistory(query: DistributionHistoryQuery): List<Distribution> =
        executeWithPaging(query, client::getDistributionHistory)

    fun getFlexibleSavingsInterestHistory(): List<FlexibleSavingsInterest> =
        getFlexibleSavingsInterestHistory(defaultTimeRange)

    fun getFlexibleSavingsInterestHistory(timeRange: Range<Instant>): List<FlexibleSavingsInterest> {
        val query = FlexibleSavingsInterestHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), client::getFlexibleSavingsInterestHistory)
    }

    fun getFlexibleSavingsInterestHistory(
        query: FlexibleSavingsInterestHistoryQuery
    ): List<FlexibleSavingsInterest> =
        executeWithPaging(query, client::getFlexibleSavingsInterestHistory)

    fun getLockedStakingInterestHistory(): List<LockedStakingInterest> =
        getLockedStakingInterestHistory(defaultTimeRange)

    fun getLockedStakingInterestHistory(timeRange: Range<Instant>): List<LockedStakingInterest> {
        val query = LockedStakingInterestHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), client::getLockedStakingInterestHistory)
    }

    fun getLockedStakingInterestHistory(query: LockedStakingInterestHistoryQuery): List<LockedStakingInterest> =
        executeWithPaging(query, client::getLockedStakingInterestHistory)

    fun getIsolatedMarginBorrowingHistory(): List<IsolatedMarginBorrowing> =
        getIsolatedMarginBorrowingHistory(defaultTimeRange)

    fun getIsolatedMarginBorrowingHistory(timeRange: Range<Instant>): List<IsolatedMarginBorrowing> {
        val query = HistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginBorrowingHistory)
    }

    fun getIsolatedMarginBorrowingHistory(query: HistoryQuery): List<IsolatedMarginBorrowing> =
        executeWithPaging(query, client::getIsolatedMarginBorrowingHistory)

    fun getIsolatedMarginRepaymentHistory(): List<IsolatedMarginRepayment> =
        getIsolatedMarginRepaymentHistory(defaultTimeRange)

    fun getIsolatedMarginRepaymentHistory(timeRange: Range<Instant>): List<IsolatedMarginRepayment> {
        val query = HistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginRepaymentHistory)
    }

    fun getIsolatedMarginRepaymentHistory(query: HistoryQuery): List<IsolatedMarginRepayment> =
        executeWithPaging(query, client::getIsolatedMarginRepaymentHistory)

    fun getIsolatedMarginTransferHistory(): List<IsolatedMarginTransfer> =
        getIsolatedMarginTransferHistory(defaultTimeRange)

    fun getIsolatedMarginTransferHistory(timeRange: Range<Instant>): List<IsolatedMarginTransfer> {
        val query = HistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginTransferHistory)
    }

    fun getIsolatedMarginTransferHistory(query: HistoryQuery): List<IsolatedMarginTransfer> =
        executeWithPaging(query, client::getIsolatedMarginTransferHistory)

    fun getIsolatedMarginInterestHistory(): List<IsolatedMarginInterest> =
        getIsolatedMarginInterestHistory(defaultTimeRange)

    fun getIsolatedMarginInterestHistory(timeRange: Range<Instant>): List<IsolatedMarginInterest> {
        val query = IsolatedMarginInterestHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginInterestHistory)
    }

    fun getIsolatedMarginInterestHistory(
        query: IsolatedMarginInterestHistoryQuery
    ): List<IsolatedMarginInterest> =
        executeWithPaging(query, client::getIsolatedMarginInterestHistory)

    fun getIsolatedMarginRebateHistory(): List<IsolatedMarginRebate> =
        getIsolatedMarginRebateHistory(defaultTimeRange)

    fun getIsolatedMarginRebateHistory(timeRange: Range<Instant>): List<IsolatedMarginRebate> {
        val query = IsolatedMarginRebateHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginRebateHistory)
    }

    fun getIsolatedMarginRebateHistory(query: IsolatedMarginRebateHistoryQuery): List<IsolatedMarginRebate> =
        executeWithPaging(query, client::getIsolatedMarginRebateHistory)

    private val defaultTimeRange: Range<Instant>
        get() = Range.closedOpen(defaultStartTime, Instant.now().truncatedTo(ChronoUnit.SECONDS))
}
