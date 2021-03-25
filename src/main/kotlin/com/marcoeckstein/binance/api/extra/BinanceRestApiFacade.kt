package com.marcoeckstein.binance.api.extra

import com.google.common.collect.Range
import com.marcoeckstein.binance.api.client.prvt.BinancePrivateApiRestClient
import com.marcoeckstein.binance.api.client.prvt.account.AccountType
import com.marcoeckstein.binance.api.client.prvt.account.AssetBalance
import com.marcoeckstein.binance.api.client.prvt.account.Distribution
import com.marcoeckstein.binance.api.client.prvt.account.FiatDepositAndWithdrawHistoryEntry
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountDetail
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountPosition
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginBorrowing
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginInterest
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginRebate
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginRepayment
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginTransfer
import com.marcoeckstein.binance.api.client.prvt.account.Order
import com.marcoeckstein.binance.api.client.prvt.account.Payment
import com.marcoeckstein.binance.api.client.prvt.account.PaymentType
import com.marcoeckstein.binance.api.client.prvt.account.Trade
import com.marcoeckstein.binance.api.client.prvt.account.WithdrawDirection
import com.marcoeckstein.binance.api.client.prvt.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.api.client.prvt.account.earn.FlexibleSavingsPosition
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingPosition
import com.marcoeckstein.binance.api.client.prvt.account.earn.request.FlexibleSavingsInterestHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.earn.request.LockedStakingInterestHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.AssetBalanceQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.DistributionHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.FiatDepositAndWithdrawHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.HistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.IsolatedMarginInterestHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.IsolatedMarginRebateHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.OpenOrdersQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.OrderHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.PagingQueryImpl
import com.marcoeckstein.binance.api.client.prvt.account.request.PaymentHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.TradeHistoryQuery
import com.marcoeckstein.binance.api.client.public.BinancePublicApiRestClient
import com.marcoeckstein.binance.api.client.public.CoinInfo
import java.time.Instant
import java.time.temporal.ChronoUnit

class BinanceRestApiFacade(
    private val publicClient: BinancePublicApiRestClient,
    private val privateClient: BinancePrivateApiRestClient,
    private val defaultStartTime: Instant,
) : BinanceApiFacade {

    fun getAllCoinsInformation(): List<CoinInfo> =
        publicClient.getAllCoinsInformation()

    fun getSpotAccountBalances(): List<AssetBalance> =
        privateClient.getSpotAccountBalances(AssetBalanceQuery(needBtcValuation = false))

    fun getIsolatedMarginAccountDetails(): List<IsolatedMarginAccountDetail> =
        privateClient.getIsolatedMarginAccountDetails()

    fun getIsolatedMarginAccountPositions(): List<IsolatedMarginAccountPosition> =
        privateClient.getIsolatedMarginAccountPositions()

    fun getOpenOrders(accountType: AccountType): List<Order> =
        privateClient.getOpenOrders(accountType)

    fun getFlexibleSavingsPositions(): List<FlexibleSavingsPosition> =
        executeWithPaging(PagingQueryImpl(pageSize = 50)) {
            privateClient.getFlexibleSavingsPositions(pageIndex = it.pageIndex, pageSize = it.pageSize)
        }

    fun getLockedStakingPositions(): List<LockedStakingPosition> =
        executeWithPaging(PagingQueryImpl(pageSize = 200)) {
            privateClient.getLockedStakingPositions(pageIndex = it.pageIndex, pageSize = it.pageSize)
        }

    fun getOpenOrders(query: OpenOrdersQuery): List<Order> =
        privateClient.getOpenOrders(query)

    override fun getFiatDepositAndWithdrawHistory(
        direction: WithdrawDirection
    ): List<FiatDepositAndWithdrawHistoryEntry> =
        getFiatDepositAndWithdrawHistory(direction, defaultTimeRange)

    fun getFiatDepositAndWithdrawHistory(
        direction: WithdrawDirection,
        timeRange: Range<Instant>
    ): List<FiatDepositAndWithdrawHistoryEntry> {
        val query = FiatDepositAndWithdrawHistoryQuery(direction, timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), privateClient::getFiatDepositAndWithdrawHistory)
    }

    fun getFiatDepositAndWithdrawHistory(
        query: FiatDepositAndWithdrawHistoryQuery
    ): List<FiatDepositAndWithdrawHistoryEntry> =
        executeWithPaging(query, privateClient::getFiatDepositAndWithdrawHistory)

    override fun getPaymentHistory(paymentType: PaymentType): List<Payment> =
        getPaymentHistory(PaymentHistoryQuery(type = paymentType))

    fun getPaymentHistory(query: PaymentHistoryQuery): List<Payment> =
        executeWithPaging(query, privateClient::getPaymentHistory)

    fun getOpenOrders(): List<Order> {
        val query = OpenOrdersQuery(AccountType.SPOT)
        val queries = listOf(
            query,
            query.copy(accountType = AccountType.CROSS_MARGIN),
            query.copy(accountType = AccountType.ISOLATED_MARGIN)
        )
        return queries.flatMap(privateClient::getOpenOrders) // no paging
    }

    override fun getOrderHistory(accountType: AccountType): List<Order> =
        getOrderHistory(defaultTimeRange, accountType)

    fun getOrderHistory(timeRange: Range<Instant>, accountTypes: AccountType): List<Order> {
        val query = OrderHistoryQuery(
            accountType = accountTypes,
            timeRange = timeRange,
            hideCancel = false
        )
        return executeWithPaging(query.splitPeriod(), privateClient::getOrderHistory)
    }

    fun getOrderHistory(query: OrderHistoryQuery): List<Order> =
        executeWithPaging(query, privateClient::getOrderHistory)

    override fun getTradeHistory(accountType: AccountType): List<Trade> =
        getTradeHistory(defaultTimeRange, accountType)

    fun getTradeHistory(timeRange: Range<Instant>, accountType: AccountType): List<Trade> {
        val query = TradeHistoryQuery(
            accountType = accountType,
            timeRange = timeRange
        )
        return executeWithPaging(query.splitPeriod(), privateClient::getTradeHistory)
    }

    fun getTradeHistory(query: TradeHistoryQuery): List<Trade> =
        executeWithPaging(query, privateClient::getTradeHistory)

    override fun getDistributionHistory(): List<Distribution> =
        getDistributionHistory(defaultTimeRange)

    fun getDistributionHistory(timeRange: Range<Instant>): List<Distribution> {
        val query = DistributionHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), privateClient::getDistributionHistory)
    }

    fun getDistributionHistory(query: DistributionHistoryQuery): List<Distribution> =
        executeWithPaging(query, privateClient::getDistributionHistory)

    override fun getFlexibleSavingsInterestHistory(): List<FlexibleSavingsInterest> =
        getFlexibleSavingsInterestHistory(defaultTimeRange)

    fun getFlexibleSavingsInterestHistory(timeRange: Range<Instant>): List<FlexibleSavingsInterest> {
        val query = FlexibleSavingsInterestHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), privateClient::getFlexibleSavingsInterestHistory)
    }

    fun getFlexibleSavingsInterestHistory(
        query: FlexibleSavingsInterestHistoryQuery
    ): List<FlexibleSavingsInterest> =
        executeWithPaging(query, privateClient::getFlexibleSavingsInterestHistory)

    override fun getLockedStakingInterestHistory(): List<LockedStakingInterest> =
        getLockedStakingInterestHistory(defaultTimeRange)

    fun getLockedStakingInterestHistory(timeRange: Range<Instant>): List<LockedStakingInterest> {
        val query = LockedStakingInterestHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), privateClient::getLockedStakingInterestHistory)
    }

    fun getLockedStakingInterestHistory(query: LockedStakingInterestHistoryQuery): List<LockedStakingInterest> =
        executeWithPaging(query, privateClient::getLockedStakingInterestHistory)

    override fun getIsolatedMarginBorrowingHistory(): List<IsolatedMarginBorrowing> =
        getIsolatedMarginBorrowingHistory(defaultTimeRange)

    fun getIsolatedMarginBorrowingHistory(timeRange: Range<Instant>): List<IsolatedMarginBorrowing> {
        val query = HistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), privateClient::getIsolatedMarginBorrowingHistory)
    }

    fun getIsolatedMarginBorrowingHistory(query: HistoryQuery): List<IsolatedMarginBorrowing> =
        executeWithPaging(query, privateClient::getIsolatedMarginBorrowingHistory)

    override fun getIsolatedMarginRepaymentHistory(): List<IsolatedMarginRepayment> =
        getIsolatedMarginRepaymentHistory(defaultTimeRange)

    fun getIsolatedMarginRepaymentHistory(timeRange: Range<Instant>): List<IsolatedMarginRepayment> {
        val query = HistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), privateClient::getIsolatedMarginRepaymentHistory)
    }

    fun getIsolatedMarginRepaymentHistory(query: HistoryQuery): List<IsolatedMarginRepayment> =
        executeWithPaging(query, privateClient::getIsolatedMarginRepaymentHistory)

    override fun getIsolatedMarginTransferHistory(): List<IsolatedMarginTransfer> =
        getIsolatedMarginTransferHistory(defaultTimeRange)

    fun getIsolatedMarginTransferHistory(timeRange: Range<Instant>): List<IsolatedMarginTransfer> {
        val query = HistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), privateClient::getIsolatedMarginTransferHistory)
    }

    fun getIsolatedMarginTransferHistory(query: HistoryQuery): List<IsolatedMarginTransfer> =
        executeWithPaging(query, privateClient::getIsolatedMarginTransferHistory)

    override fun getIsolatedMarginInterestHistory(): List<IsolatedMarginInterest> =
        getIsolatedMarginInterestHistory(defaultTimeRange)

    fun getIsolatedMarginInterestHistory(timeRange: Range<Instant>): List<IsolatedMarginInterest> {
        val query = IsolatedMarginInterestHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), privateClient::getIsolatedMarginInterestHistory)
    }

    fun getIsolatedMarginInterestHistory(
        query: IsolatedMarginInterestHistoryQuery
    ): List<IsolatedMarginInterest> =
        executeWithPaging(query, privateClient::getIsolatedMarginInterestHistory)

    override fun getIsolatedMarginRebateHistory(): List<IsolatedMarginRebate> =
        getIsolatedMarginRebateHistory(defaultTimeRange)

    fun getIsolatedMarginRebateHistory(timeRange: Range<Instant>): List<IsolatedMarginRebate> {
        val query = IsolatedMarginRebateHistoryQuery(timeRange = timeRange)
        return executeWithPaging(query.splitPeriod(), privateClient::getIsolatedMarginRebateHistory)
    }

    fun getIsolatedMarginRebateHistory(query: IsolatedMarginRebateHistoryQuery): List<IsolatedMarginRebate> =
        executeWithPaging(query, privateClient::getIsolatedMarginRebateHistory)

    private val defaultTimeRange: Range<Instant>
        get() = Range.closedOpen(defaultStartTime, Instant.now().truncatedTo(ChronoUnit.SECONDS))
}
