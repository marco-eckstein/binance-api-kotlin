package com.marcoeckstein.binance.api.extra

import com.marcoeckstein.binance.api.client.prvt.account.AccountType
import com.marcoeckstein.binance.api.client.prvt.account.Distribution
import com.marcoeckstein.binance.api.client.prvt.account.FiatDepositAndWithdrawHistoryEntry
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
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingInterest

interface BinanceApiFacade {

    fun getFiatDepositAndWithdrawHistory(
        direction: WithdrawDirection
    ): List<FiatDepositAndWithdrawHistoryEntry>

    fun getPaymentHistory(): List<Payment> =
        PaymentType.values().flatMap(::getPaymentHistory).sortedBy { it.timestamp }

    fun getPaymentHistory(paymentType: PaymentType): List<Payment>

    fun getOrderHistory(): List<Order> =
        AccountType.values().flatMap(::getOrderHistory).sortedBy { it.timestamp }

    fun getOrderHistory(accountType: AccountType): List<Order>

    fun getTradeHistory(): List<Trade> =
        AccountType.values().flatMap(::getTradeHistory).sortedBy { it.timestamp }

    fun getTradeHistory(accountType: AccountType): List<Trade>

    fun getDistributionHistory(): List<Distribution>

    fun getFlexibleSavingsInterestHistory(): List<FlexibleSavingsInterest>

    fun getLockedStakingInterestHistory(): List<LockedStakingInterest>

    fun getIsolatedMarginBorrowingHistory(): List<IsolatedMarginBorrowing>

    fun getIsolatedMarginRepaymentHistory(): List<IsolatedMarginRepayment>

    fun getIsolatedMarginTransferHistory(): List<IsolatedMarginTransfer>

    fun getIsolatedMarginInterestHistory(): List<IsolatedMarginInterest>

    fun getIsolatedMarginRebateHistory(): List<IsolatedMarginRebate>
}
