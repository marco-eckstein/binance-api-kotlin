package com.marcoeckstein.binance.api.extra

import com.google.common.collect.BoundType
import com.marcoeckstein.binance.api.client.prvt.account.AccountType
import com.marcoeckstein.binance.api.client.prvt.account.Timestamped
import com.marcoeckstein.binance.api.client.prvt.account.WithdrawDirection
import com.marcoeckstein.binance.api.client.prvt.account.earn.request.FlexibleSavingsInterestHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.earn.request.LockedStakingInterestHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.DistributionHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.FiatDepositAndWithdrawHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.HistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.IsolatedMarginInterestHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.IsolatedMarginRebateHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.OrderHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.PeriodQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.TradeHistoryQuery
import com.marcoeckstein.binance.api.facade
import com.marcoeckstein.klib.algorithm.findNumber
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.Test
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ExperimentalSerializationApi
class BinancePrivateApiFacadePeriodTests {

    companion object {

        private const val sampleSize = 3
    }

    @Test
    fun `getFiatDepositAndWithdrawHistory period is as expected`() {
        val history = facade.getFiatDepositAndWithdrawHistory(WithdrawDirection.DEPOSIT)
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            checkPeriods(
                FiatDepositAndWithdrawHistoryQuery(WithdrawDirection.DEPOSIT),
                it,
                facade::getFiatDepositAndWithdrawHistory
            )
        }
    }

    @Test
    fun `getOrderHistory period is as expected`() {
        val history = facade.getOrderHistory(AccountType.SPOT)
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            val query = OrderHistoryQuery(
                accountType = AccountType.SPOT,
                baseAsset = it.baseAsset,
                quoteAsset = it.quoteAsset,
                direction = it.side,
                hideCancel = false
            )
            checkPeriods(query, it, facade::getOrderHistory)
        }
    }

    @Test
    fun `getTradeHistory period is as expected`() {
        val history = facade.getTradeHistory(AccountType.SPOT)
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            val query = TradeHistoryQuery(
                accountType = AccountType.SPOT,
                baseAsset = it.baseAsset,
                quoteAsset = it.quoteAsset,
                direction = it.side
            )
            checkPeriods(query, it, facade::getTradeHistory)
        }
    }

    @Test
    fun `getDistributionHistory period is as expected`() {
        val history = facade.getDistributionHistory()
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            checkPeriods(
                DistributionHistoryQuery(asset = it.asset),
                it,
                facade::getDistributionHistory
            )
        }
    }

    @Test
    fun `getFlexibleSavingsInterestHistory period is as expected`() {
        val history = facade.getFlexibleSavingsInterestHistory()
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            checkPeriods(
                FlexibleSavingsInterestHistoryQuery(asset = it.asset),
                it,
                facade::getFlexibleSavingsInterestHistory,
                allowOffset = true
            )
        }
    }

    @Test
    fun `getLockedStakingInterestHistory period is as expected`() {
        val history = facade.getLockedStakingInterestHistory()
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            checkPeriods(
                LockedStakingInterestHistoryQuery(asset = it.asset),
                it,
                facade::getLockedStakingInterestHistory,
                allowOffset = true
            )
        }
    }

    @Test
    fun `getIsolatedMarginBorrowingHistory period is as expected`() {
        val history = facade.getIsolatedMarginBorrowingHistory()
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            checkPeriods(
                HistoryQuery(),
                it,
                facade::getIsolatedMarginBorrowingHistory,
                allowOffset = true
            )
        }
    }

    @Test
    fun `getIsolatedMarginRepaymentHistory period is as expected`() {
        val history = facade.getIsolatedMarginRepaymentHistory()
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            checkPeriods(
                HistoryQuery(),
                it,
                facade::getIsolatedMarginRepaymentHistory,
                allowOffset = true
            )
        }
    }

    @Test
    fun `getIsolatedMarginTransferHistory period is as expected`() {
        val history = facade.getIsolatedMarginTransferHistory()
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            checkPeriods(
                HistoryQuery(),
                it,
                facade::getIsolatedMarginTransferHistory
            )
        }
    }

    @Test
    fun `getIsolatedMarginInterestHistory period is as expected`() {
        val history = facade.getIsolatedMarginInterestHistory()
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            checkPeriods(
                IsolatedMarginInterestHistoryQuery(),
                it,
                facade::getIsolatedMarginInterestHistory
            )
        }
    }

    @Test
    fun `getIsolatedMarginRebateHistory period is as expected`() {
        val history = facade.getIsolatedMarginRebateHistory()
        history shouldHaveAtLeastSize 1
        history.take(sampleSize).forEach {
            checkPeriods(
                IsolatedMarginRebateHistoryQuery(asset = it.asset),
                it,
                facade::getIsolatedMarginRebateHistory
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
