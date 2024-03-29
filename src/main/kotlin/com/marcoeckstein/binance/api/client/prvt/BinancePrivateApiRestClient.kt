package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.client.BinanceApiException
import com.marcoeckstein.binance.api.client.prvt.account.AccountType
import com.marcoeckstein.binance.api.client.prvt.account.AssetBalance
import com.marcoeckstein.binance.api.client.prvt.account.CrossMarginBorrowing
import com.marcoeckstein.binance.api.client.prvt.account.CrossMarginRepayment
import com.marcoeckstein.binance.api.client.prvt.account.CrossMarginTransfer
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
import com.marcoeckstein.binance.api.client.prvt.account.PaidInterest
import com.marcoeckstein.binance.api.client.prvt.account.Payment
import com.marcoeckstein.binance.api.client.prvt.account.Trade
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
import com.marcoeckstein.binance.api.client.prvt.account.request.PaymentHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.TradeHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult
import retrofit2.Call

class BinancePrivateApiRestClient internal constructor(
    private val headers: Map<String, String>,
    private val exchangeApiV1Service: ExchangeApiV1Service,
    private val gatewayApiV1Service: GatewayApiV1Service,
    private val gatewayApiV3Service: GatewayApiV3Service,
    private val fiatApiV1Service: FiatApiV1Service,
    private val assetApiV1Service: AssetApiV1Service,
    private val earnApiV1Service: EarnApiV1Service,
    private val marginApiV1Service: MarginApiV1Service,
) {

    /**
     * Get spot balances
     *
     * Web:
     * - [Fiat and Spot](https://www.binance.com/en/my/wallet/account/main)
     */
    fun getSpotAccountBalances(query: AssetBalanceQuery): List<AssetBalance> =
        execute(gatewayApiV3Service.getSpotAccountBalances(headers, query))

    /**
     * Get isolated margin account details
     *
     * Web:
     * - [Margin | Isolated | Funds](https://www.binance.com/en/my/wallet/account/margin/isolated)
     */
    fun getIsolatedMarginAccountDetails(): List<IsolatedMarginAccountDetail> =
        execute(gatewayApiV1Service.getIsolatedMarginAccountDetails(headers))

    /**
     * Get isolated margins account positions
     *
     * Web:
     * - [Margin | Isolated | Positions](https://www.binance.com/en/my/wallet/account/margin/isolated)
     */
    fun getIsolatedMarginAccountPositions(): List<IsolatedMarginAccountPosition> =
        execute(gatewayApiV1Service.getIsolatedMarginAccountPositions(headers))

    /**
     * Get flexible savings positions
     *
     * Web:
     * - [Earn | Savings | Flexible](https://www.binance.com/en/my/wallet/account/saving)
     *
     * @param pageSize Max: 50
     */
    fun getFlexibleSavingsPositions(pageIndex: Int, pageSize: Int = 50): List<FlexibleSavingsPosition> =
        execute(gatewayApiV1Service.getFlexibleSavingsPositions(headers, pageIndex, pageSize))

    /**
     * Get locked staking positions
     *
     * Web:
     * - [Earn | Locked Staking | Locked](https://www.binance.com/en/my/wallet/account/saving)
     *
     * @param pageSize Max: 200
     */
    fun getLockedStakingPositions(pageIndex: Int, pageSize: Int = 200): List<LockedStakingPosition> =
        execute(gatewayApiV1Service.getLockedStakingPositions(headers, pageIndex, pageSize))

    fun getOpenOrders(accountType: AccountType): List<Order> =
        getOpenOrders(OpenOrdersQuery(accountType))

    /**
     * Get open orders
     *
     * Web:
     * - [Spot Order | Open Orders](https://www.binance.com/en/my/orders/exchange/openorder)
     * - [Margin Orders | Open Orders | Cross](https://www.binance.com/en/my/orders/margin/openorder/margin)
     * - [Margin Orders | Open Orders | Isolated](https://www.binance.com/en/my/orders/margin/openorder/isolated_margin)
     */
    fun getOpenOrders(query: OpenOrdersQuery): List<Order> =
        execute(exchangeApiV1Service.getOpenOrders(headers, query))

    /**
     * Get order history (orders that are not open)
     *
     * Web:
     * - [Spot Order | Order History]()https://www.binance.com/en/my/orders/exchange/tradeorder)
     * - [Margin Orders | Order History | Cross]()https://www.binance.com/en/my/orders/margin/tradeorder/margin)
     * - [Margin Orders | Order History | Isolated]()https://www.binance.com/en/my/orders/margin/tradeorder/isolated_margin)
     */
    fun getOrderHistory(query: OrderHistoryQuery): List<Order> =
        execute(exchangeApiV1Service.getOrderHistory(headers, query))

    /**
     * Get trade history
     *
     * Web:
     * - [Spot Order | Trade History](https://www.binance.com/en/my/orders/exchange/usertrade)
     * - [Margin Orders | Trade History | Cross](https://www.binance.com/en/my/orders/margin/usertrade/margin)
     * - [Margin Orders | Trade History | Isolated](https://www.binance.com/en/my/orders/margin/usertrade/isolated_margin)
     */
    fun getTradeHistory(query: TradeHistoryQuery): List<Trade> =
        execute(exchangeApiV1Service.getTradeHistory(headers, query))

    /**
     * Get cross margin borrow history
     *
     * Web:
     * - [Margin Orders | Borrowing | Cross](https://www.binance.com/en/my/orders/margin/borrow/margin)
     */
    fun getCrossMarginBorrowHistory(query: HistoryQuery): List<CrossMarginBorrowing> =
        execute(
            exchangeApiV1Service.getCrossMarginBorrowHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli()
            )
        )

    /**
     * Get isolated margin borrow history
     *
     * Web:
     * - [Margin Orders | Borrowing | Isolated](https://www.binance.com/en/my/orders/margin/borrow/isolated_margin)
     */
    fun getIsolatedMarginBorrowingHistory(query: HistoryQuery): List<IsolatedMarginBorrowing> =
        execute(
            exchangeApiV1Service.getIsolatedMarginBorrowingHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli()
            )
        )

    /**
     * Get cross margin repay history
     *
     * Web:
     * - [Margin Orders | Repayment | Cross](https://www.binance.com/en/my/orders/margin/repayment/margin)
     */
    fun getCrossMarginRepayHistory(query: HistoryQuery): List<CrossMarginRepayment> =
        execute(
            exchangeApiV1Service.getCrossMarginRepayHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli()
            )
        )

    /**
     * Get isolated margin repay history
     *
     * Web:
     * - [Margin Orders | Repayment | Isolated](https://www.binance.com/en/my/orders/margin/repayment/isolated_margin)
     */
    fun getIsolatedMarginRepaymentHistory(query: HistoryQuery): List<IsolatedMarginRepayment> =
        execute(
            exchangeApiV1Service.getIsolatedMarginRepaymentHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli()
            )
        )

    /**
     * Get cross margin transfer history
     *
     * Web:
     * - [Margin Orders | Transfers | Cross](https://www.binance.com/en/my/orders/margin/transfer/margin)
     */
    fun getCrossMarginTransferHistory(query: HistoryQuery): List<CrossMarginTransfer> =
        execute(
            exchangeApiV1Service.getCrossMarginTransferHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli()
            )
        )

    /**
     * Get isolated margin transfer history
     *
     * Web:
     * - [Margin Orders | Transfers | Isolated](https://www.binance.com/en/my/orders/margin/transfer/isolated_margin)
     */
    fun getIsolatedMarginTransferHistory(query: HistoryQuery): List<IsolatedMarginTransfer> =
        execute(
            exchangeApiV1Service.getIsolatedMarginTransferHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli()
            )
        )

    /**
     * Get cross margin interest history
     *
     * Web:
     * - [Margin Orders | Interest | Cross](https://www.binance.com/en/my/orders/margin/interest/margin)
     */
    fun getCrossMarginInterestHistory(query: HistoryQuery): List<PaidInterest> =
        execute(
            exchangeApiV1Service.getCrossMarginInterestHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli()
            )
        )

    /**
     * Get isolated margin interest history
     *
     * Web:
     * - [Margin Orders | Interest | Isolated](https://www.binance.com/en/my/orders/margin/interest/isolated_margin)
     */
    fun getIsolatedMarginInterestHistory(
        query: IsolatedMarginInterestHistoryQuery
    ): List<IsolatedMarginInterest> =
        execute(
            marginApiV1Service.getIsolatedMarginInterestHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli(),
                query.archived,
            )
        )

    /**
     * Get fiat deposit & withdraw history
     *
     * Web:
     * - [Transaction History | Fiat | Deposit & Withdraw](https://www.binance.com/en/my/wallet/history/withdraw-fiat)
     */
    fun getFiatDepositAndWithdrawHistory(
        query: FiatDepositAndWithdrawHistoryQuery
    ): List<FiatDepositAndWithdrawHistoryEntry> =
        execute(fiatApiV1Service.getFiatDepositAndWithdrawHistory(headers, query))

    /**
     * Get distribution history
     *
     * Web:
     * - [Transaction History | Distribution | Fiat and Spot](https://www.binance.com/en/my/wallet/history/distribution)
     */
    fun getDistributionHistory(query: DistributionHistoryQuery): List<Distribution> =
        execute(assetApiV1Service.getDistributionHistory(headers, query))

    /**
     * Get payment history (aka buy crypto history)
     *
     * Web:
     * - [Buy Crypto History | Buy/Sell](https://www.binance.com/en/my/wallet/exchange/buysell-history)
     */
    fun getPaymentHistory(query: PaymentHistoryQuery): List<Payment> =
        execute(fiatApiV1Service.getPaymentHistory(headers, query))

    /**
     * Get flexible savings interest history
     *
     * Web:
     * - [Earn History | Savings | Flexible | Interest ](https://www.binance.com/en/my/saving/history/savings)
     */
    fun getFlexibleSavingsInterestHistory(
        query: FlexibleSavingsInterestHistoryQuery
    ): List<FlexibleSavingsInterest> =
        execute(
            earnApiV1Service.getFlexibleSavingsInterestHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.asset,
                query.lendingType,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli(),
            )
        )

    /**
     * Get locked staking interest history
     *
     * Web:
     * - [Earn History | Staking | Locked Staking | Interest](https://www.binance.com/en/my/saving/history/staking)
     */
    fun getLockedStakingInterestHistory(query: LockedStakingInterestHistoryQuery): List<LockedStakingInterest> =
        execute(
            earnApiV1Service.getLockedStakingInterestHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.asset,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli(),
            )
        )

    /**
     * Get isolated margin rebate history (aka discount or returned fees history)
     *
     * Web:
     * - [Margin Orders | Fees Return History](https://www.binance.com/en/my/orders/margin/fee-return-history)
     */
    fun getIsolatedMarginRebateHistory(query: IsolatedMarginRebateHistoryQuery): List<IsolatedMarginRebate> =
        execute(
            marginApiV1Service.getIsolatedMarginRebateHistory(
                headers,
                query.pageIndex,
                query.pageSize,
                query.asset,
                query.symbol,
                query.startTime?.toEpochMilli(),
                query.endTime?.toEpochMilli(),
            )
        )

    private fun <T : Any> execute(call: Call<out QueryResult<T>>): List<T> {
        val response = call.execute()
        if (!response.isSuccessful)
            throw BinanceApiException(response.code().toString() + " " + response.errorBody()?.string())
        val result = response.body()!!
        if (!result.success) {
            throw BinanceApiException(result.message ?: "")
        }
        return result.items
    }
}
