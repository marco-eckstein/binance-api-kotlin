package com.marcoeckstein.binance.api.extra

import com.marcoeckstein.binance.api.client.prvt.account.WithdrawDirection
import com.marcoeckstein.binance.api.facade
import io.kotest.matchers.collections.shouldBeSameSizeAs
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExperimentalSerializationApi
class BinancePrivateApiFacadeDuplicateTests {

    companion object {

        @JvmStatic
        val calls = listOf<BinanceRestApiFacade.() -> List<Any>>(
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
    fun `result list does not have duplicated`(call: BinanceRestApiFacade.() -> List<Any>) {
        val resultList = facade.call()
        resultList shouldBeSameSizeAs resultList.distinct()
    }
}
