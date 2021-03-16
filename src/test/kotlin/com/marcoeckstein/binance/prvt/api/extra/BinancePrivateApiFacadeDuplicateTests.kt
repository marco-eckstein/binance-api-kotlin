package com.marcoeckstein.binance.prvt.api.extra

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
            { getFiatDepositAndWithdrawHistory(WithdrawDirection.DEPOSIT) },
            { getFiatDepositAndWithdrawHistory(WithdrawDirection.WITHDRAW) },
            { getOrderHistory() },
            { getTradeHistory() },
            { getDistributionHistory() },
            { getFlexibleSavingsInterestHistory() },
            { getLockedStakingInterestHistory() },
            { getIsolatedMarginBorrowingHistory() },
            { getIsolatedMarginRepaymentHistory() },
            { getIsolatedMarginTransferHistory() },
            { getIsolatedMarginInterestHistory() },
            { getIsolatedMarginRebateHistory() },
        )
    }

    @ParameterizedTest
    @MethodSource("getCalls")
    fun `result list does not have duplicated`(call: BinancePrivateApiFacade.() -> List<Any>) {
        val resultList = privateApi.call()
        resultList shouldBeSameSizeAs resultList.distinct()
    }
}
