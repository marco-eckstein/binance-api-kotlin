package com.marcoeckstein.binance.prvt.api.extra

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

class BinancePrivateApiFacade(
    private val client: BinancePrivateApiRestClient
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
        startTime: Instant,
        direction: WithdrawDirection
    ): List<FiatDepositAndWithdrawHistoryEntry> {
        val query = FiatDepositAndWithdrawHistoryQuery(
            direction = direction,
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        )
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
        startTime: Instant,
        accountTypes: Set<AccountType> =
            setOf(AccountType.SPOT, AccountType.CROSS_MARGIN, AccountType.ISOLATED_MARGIN)
    ): List<Order> {
        val query = OrderHistoryQuery(
            accountType = AccountType.SPOT,
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
            hideCancel = false
        )
        val queries = accountTypes.map { query.copy(accountType = it) }.flatMap { it.splitPeriod() }
        return executeWithPaging(queries, client::getOrderHistory)
    }

    fun getOrderHistory(query: OrderHistoryQuery): List<Order> =
        executeWithPaging(query, client::getOrderHistory)

    fun getTradeHistory(
        startTime: Instant,
        accountTypes: Set<AccountType> =
            setOf(AccountType.SPOT, AccountType.CROSS_MARGIN, AccountType.ISOLATED_MARGIN)
    ): List<Trade> {
        val query = TradeHistoryQuery(
            accountType = AccountType.SPOT,
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        )
        val queries = accountTypes.map { query.copy(accountType = it) }.flatMap { it.splitPeriod() }
        return executeWithPaging(queries, client::getTradeHistory)
    }

    fun getTradeHistory(query: TradeHistoryQuery): List<Trade> =
        executeWithPaging(query, client::getTradeHistory)

    fun getDistributionHistory(startTime: Instant): List<Distribution> {
        val query = DistributionHistoryQuery(
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        )
        return executeWithPaging(query.splitPeriod(), client::getDistributionHistory)
    }

    fun getDistributionHistory(query: DistributionHistoryQuery): List<Distribution> =
        executeWithPaging(query, client::getDistributionHistory)

    fun getFlexibleSavingsInterestHistory(startTime: Instant): List<FlexibleSavingsInterest> {
        val query = FlexibleSavingsInterestHistoryQuery(
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        )
        return executeWithPaging(query.splitPeriod(), client::getFlexibleSavingsInterestHistory)
    }

    fun getFlexibleSavingsInterestHistory(
        query: FlexibleSavingsInterestHistoryQuery
    ): List<FlexibleSavingsInterest> =
        executeWithPaging(query, client::getFlexibleSavingsInterestHistory)

    fun getLockedStakingInterestHistory(startTime: Instant): List<LockedStakingInterest> {
        val query = LockedStakingInterestHistoryQuery(
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        )
        return executeWithPaging(query.splitPeriod(), client::getLockedStakingInterestHistory)
    }

    fun getLockedStakingInterestHistory(query: LockedStakingInterestHistoryQuery): List<LockedStakingInterest> =
        executeWithPaging(query, client::getLockedStakingInterestHistory)

    fun getIsolatedMarginBorrowingHistory(startTime: Instant): List<IsolatedMarginBorrowing> {
        val query = HistoryQuery(
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        )
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginBorrowingHistory)
    }

    fun getIsolatedMarginBorrowingHistory(query: HistoryQuery): List<IsolatedMarginBorrowing> =
        executeWithPaging(query, client::getIsolatedMarginBorrowingHistory)

    fun getIsolatedMarginRepaymentHistory(startTime: Instant): List<IsolatedMarginRepayment> {
        val query = HistoryQuery(
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        )
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginRepaymentHistory)
    }

    fun getIsolatedMarginRepaymentHistory(query: HistoryQuery): List<IsolatedMarginRepayment> =
        executeWithPaging(query, client::getIsolatedMarginRepaymentHistory)

    fun getIsolatedMarginTransferHistory(startTime: Instant): List<IsolatedMarginTransfer> {
        val query = HistoryQuery(
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        )
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginTransferHistory)
    }

    fun getIsolatedMarginTransferHistory(query: HistoryQuery): List<IsolatedMarginTransfer> =
        executeWithPaging(query, client::getIsolatedMarginTransferHistory)

    fun getIsolatedMarginInterestHistory(startTime: Instant): List<IsolatedMarginInterest> {
        val query = IsolatedMarginInterestHistoryQuery(
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.SECONDS),
        )
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginInterestHistory)
    }

    fun getIsolatedMarginInterestHistory(
        query: IsolatedMarginInterestHistoryQuery
    ): List<IsolatedMarginInterest> =
        executeWithPaging(query, client::getIsolatedMarginInterestHistory)

    fun getIsolatedMarginRebateHistory(startTime: Instant): List<IsolatedMarginRebate> {
        val query = IsolatedMarginRebateHistoryQuery(
            startTime = startTime,
            endTime = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        )
        return executeWithPaging(query.splitPeriod(), client::getIsolatedMarginRebateHistory)
    }

    fun getIsolatedMarginRebateHistory(query: IsolatedMarginRebateHistoryQuery): List<IsolatedMarginRebate> =
        executeWithPaging(query, client::getIsolatedMarginRebateHistory)
}
