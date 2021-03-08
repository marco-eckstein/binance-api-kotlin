package com.marcoeckstein.binance.prvt.api.extra

import com.marcoeckstein.binance.prvt.api.client.account.AccountType
import com.marcoeckstein.binance.prvt.api.client.account.WithdrawDirection
import com.marcoeckstein.binance.prvt.api.config
import com.marcoeckstein.binance.prvt.api.privateApi
import io.kotest.matchers.collections.shouldBeSameSizeAs
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExperimentalSerializationApi
class BinancePrivateApiFacadeDuplicateTests {

    companion object {

        private val startTime = config.accountStartTime

        @JvmStatic
        val calls = listOf<BinancePrivateApiFacade.() -> List<Any>>(
            { getFiatDepositAndWithdrawHistory(startTime, WithdrawDirection.DEPOSIT) },
            { getFiatDepositAndWithdrawHistory(startTime, WithdrawDirection.CASH_TRANSFER) },
            { getFiatDepositAndWithdrawHistory(startTime, WithdrawDirection.WITHDRAW) },
            { getOrderHistory(startTime) },
            { getTradeHistory(startTime) },
            { getDistributionHistory(startTime) },
            { getFlexibleSavingsInterestHistory(startTime) },
            { getLockedStakingInterestHistory(startTime) },
            { getIsolatedMarginBorrowingHistory(startTime) },
            { getIsolatedMarginRepaymentHistory(startTime) },
            { getIsolatedMarginTransferHistory(startTime) },
            { getIsolatedMarginInterestHistory(startTime) },
            { getIsolatedMarginRebateHistory(startTime) },
        )
    }

    @ParameterizedTest
    @MethodSource("getCalls")
    fun `result list does not have duplicated`(call: BinancePrivateApiFacade.() -> List<Any>) {
        val resultList = privateApi.call()
        resultList shouldBeSameSizeAs resultList.distinct()
    }
}
