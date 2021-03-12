package com.marcoeckstein.binance.prvt.api.extra

import com.google.common.collect.BoundType
import com.marcoeckstein.binance.prvt.api.client.account.AccountType
import com.marcoeckstein.binance.prvt.api.client.account.Timestamped
import com.marcoeckstein.binance.prvt.api.client.account.WithdrawDirection
import com.marcoeckstein.binance.prvt.api.client.account.earn.request.FlexibleSavingsInterestHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.earn.request.LockedStakingInterestHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.DistributionHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.FiatDepositAndWithdrawHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.HistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.IsolatedMarginInterestHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.IsolatedMarginRebateHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.OrderHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.PeriodQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.TradeHistoryQuery
import com.marcoeckstein.binance.prvt.api.privateApi
import com.marcoeckstein.klib.algorithm.findNumber
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.Test

@ExperimentalSerializationApi
class BinancePrivateApiFacadePeriodTests {

    companion object {

        private const val sampleSize = 3
    }

    @Test
    fun `getFiatDepositAndWithdrawHistory period is as expected`() {
        privateApi.getFiatDepositAndWithdrawHistory(WithdrawDirection.DEPOSIT).take(sampleSize).forEach {
            checkPeriods(
                FiatDepositAndWithdrawHistoryQuery(WithdrawDirection.DEPOSIT),
                it,
                privateApi::getFiatDepositAndWithdrawHistory
            )
        }
    }

    @Test
    fun `getOrderHistory period is as expected`() {
        privateApi.getOrderHistory(setOf(AccountType.SPOT)).take(sampleSize).forEach {
            val query = OrderHistoryQuery(
                accountType = AccountType.SPOT,
                baseAsset = it.baseAsset,
                quoteAsset = it.quoteAsset,
                direction = it.side,
                hideCancel = false
            )
            checkPeriods(query, it, privateApi::getOrderHistory)
        }
    }

    @Test
    fun `getTradeHistory period is as expected`() {
        privateApi.getTradeHistory(setOf(AccountType.SPOT)).take(sampleSize).forEach {
            val query = TradeHistoryQuery(
                accountType = AccountType.SPOT,
                baseAsset = it.baseAsset,
                quoteAsset = it.quoteAsset,
                direction = it.side
            )
            checkPeriods(query, it, privateApi::getTradeHistory)
        }
    }

    @Test
    fun `getDistributionHistory period is as expected`() {
        privateApi.getDistributionHistory().take(sampleSize).forEach {
            checkPeriods(
                DistributionHistoryQuery(asset = it.asset),
                it,
                privateApi::getDistributionHistory
            )
        }
    }

    @Test
    fun `getFlexibleSavingsInterestHistory period is as expected`() {
        privateApi.getFlexibleSavingsInterestHistory().take(sampleSize).forEach {
            checkPeriods(
                FlexibleSavingsInterestHistoryQuery(asset = it.asset),
                it,
                privateApi::getFlexibleSavingsInterestHistory,
                allowOffset = true
            )
        }
    }

    @Test
    fun `getLockedStakingInterestHistory period is as expected`() {
        privateApi.getLockedStakingInterestHistory().take(sampleSize).forEach {
            checkPeriods(
                LockedStakingInterestHistoryQuery(asset = it.asset),
                it,
                privateApi::getLockedStakingInterestHistory,
                allowOffset = true
            )
        }
    }

    @Test
    fun `getIsolatedMarginBorrowingHistory period is as expected`() {
        privateApi.getIsolatedMarginBorrowingHistory().take(sampleSize).forEach {
            checkPeriods(
                HistoryQuery(),
                it,
                privateApi::getIsolatedMarginBorrowingHistory,
                allowOffset = true
            )
        }
    }

    @Test
    fun `getIsolatedMarginRepaymentHistory period is as expected`() {
        privateApi.getIsolatedMarginRepaymentHistory().take(sampleSize).forEach {
            checkPeriods(
                HistoryQuery(),
                it,
                privateApi::getIsolatedMarginRepaymentHistory,
                allowOffset = true
            )
        }
    }

    @Test
    fun `getIsolatedMarginTransferHistory period is as expected`() {
        privateApi.getIsolatedMarginTransferHistory().take(sampleSize).forEach {
            checkPeriods(
                HistoryQuery(),
                it,
                privateApi::getIsolatedMarginTransferHistory
            )
        }
    }

    @Test
    fun `getIsolatedMarginInterestHistory period is as expected`() {
        privateApi.getIsolatedMarginInterestHistory().take(sampleSize).forEach {
            checkPeriods(
                IsolatedMarginInterestHistoryQuery(),
                it,
                privateApi::getIsolatedMarginInterestHistory
            )
        }
    }

    @Test
    fun `getIsolatedMarginRebateHistory period is as expected`() {
        privateApi.getIsolatedMarginRebateHistory().take(sampleSize).forEach {
            checkPeriods(
                IsolatedMarginRebateHistoryQuery(asset = it.asset),
                it,
                privateApi::getIsolatedMarginRebateHistory
            )
        }
    }

    private fun <TQuery : PeriodQuery<TQuery>, TItem : Timestamped> checkPeriods(
        query: TQuery,
        item: TItem,
        call: (TQuery) -> Collection<TItem>,
        allowOffset: Boolean = false
    ) {
        val offsetMillis = if (allowOffset) findOffsetMillis(query, item, call) else 0
        val time = item.timestamp.plusMillis(offsetMillis)
        val resolution = query.periodInfo.timestampResolution
        time shouldBe time.truncatedTo(resolution)
        call(
            query.copyWith(startTime = time, endTime = time.plus(1, resolution))
        ) shouldContain item
        call(
            query.copyWith(startTime = time.minus(1, resolution), endTime = time.minus(1, resolution))
        ) shouldNotContain item
        call(
            query.copyWith(startTime = time.plus(1, resolution), endTime = time.plus(1, resolution))
        ) shouldNotContain item
        call(query.copyWith(startTime = time, endTime = time)).also {
            if (query.periodInfo.endTimeType == BoundType.CLOSED)
                it shouldContain item
            else
                it shouldNotContain item
        }
    }

    private fun <TQuery : PeriodQuery<TQuery>, TItem : Timestamped> findOffsetMillis(
        query: TQuery,
        item: TItem,
        call: (TQuery) -> Collection<TItem>
    ): Long =
        findNumber { range ->
            val items = call(
                query.copyWith(
                    startTime = item.timestamp.plusMillis(range.first.toLong()),
                    endTime = item.timestamp.plusMillis(range.last.toLong() + 1)
                )
            )
            item in items
        }
            ?.toLong()
            ?: throw AssertionError("Cannot find offset")
}
