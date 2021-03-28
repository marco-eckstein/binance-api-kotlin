@file:Suppress("LongLine", "Reformat")
/* ktlint-disable max-line-length */

package com.marcoeckstein.binance.api.extra.export

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
import com.marcoeckstein.binance.api.client.prvt.account.Trade
import com.marcoeckstein.binance.api.client.prvt.account.WithdrawDirection
import com.marcoeckstein.binance.api.client.prvt.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingInterest

// @formatter:off
internal val itemInfos: Set<ItemInfo<*>> = setOf(
    ItemInfo.of(FiatDepositAndWithdrawHistoryEntry.serializer(), "fiat-deposit-history", WithdrawDirection.DEPOSIT) { getFiatDepositAndWithdrawHistory(it, WithdrawDirection.DEPOSIT) },
    ItemInfo.of(FiatDepositAndWithdrawHistoryEntry.serializer(), "fiat-withdraw-history", WithdrawDirection.WITHDRAW) { getFiatDepositAndWithdrawHistory(it, WithdrawDirection.WITHDRAW) },
    ItemInfo.of(Payment.serializer()) { getPaymentHistory(it) },
    ItemInfo.of(Order.serializer(), "spot-order-history", AccountType.SPOT) { getOrderHistory(it, AccountType.SPOT) },
    ItemInfo.of(Order.serializer(), "cross-margin-order-history", AccountType.CROSS_MARGIN) { getOrderHistory(it, AccountType.CROSS_MARGIN) },
    ItemInfo.of(Order.serializer(), "isolated-margin-order-history", AccountType.ISOLATED_MARGIN) { getOrderHistory(it, AccountType.ISOLATED_MARGIN) },
    ItemInfo.of(Trade.serializer(), "spot-trade-history", AccountType.SPOT) { getTradeHistory(it, AccountType.SPOT) },
    ItemInfo.of(Trade.serializer(), "cross-margin-trade-history", AccountType.CROSS_MARGIN) { getTradeHistory(it, AccountType.CROSS_MARGIN) },
    ItemInfo.of(Trade.serializer(), "isolated-margin-trade-history", AccountType.ISOLATED_MARGIN) { getTradeHistory(it, AccountType.ISOLATED_MARGIN) },
    ItemInfo.of(Distribution.serializer()) { getDistributionHistory(it) },
    ItemInfo.of(FlexibleSavingsInterest.serializer()) { getFlexibleSavingsInterestHistory(it) },
    ItemInfo.of(LockedStakingInterest.serializer()) { getLockedStakingInterestHistory(it) },
    ItemInfo.of(IsolatedMarginBorrowing.serializer()) { getIsolatedMarginBorrowingHistory(it) },
    ItemInfo.of(IsolatedMarginRepayment.serializer()) { getIsolatedMarginRepaymentHistory(it) },
    ItemInfo.of(IsolatedMarginTransfer.serializer(),) { getIsolatedMarginTransferHistory(it) },
    ItemInfo.of(IsolatedMarginInterest.serializer()) { getIsolatedMarginInterestHistory(it) },
    ItemInfo.of(IsolatedMarginRebate.serializer()) { getIsolatedMarginRebateHistory(it) },
)

@Suppress("UNCHECKED_CAST")
internal inline fun <reified T : Any> getSingleItemInfo(): ItemInfo<T> =
    itemInfos.single { it.clazz == T::class } as ItemInfo<T>

@Suppress("UNCHECKED_CAST")
internal inline fun <reified T : Any> getSingleItemInfo(discriminator: Any): ItemInfo<T> =
    itemInfos.single { it.clazz == T::class && it.discriminator == discriminator } as ItemInfo<T>
