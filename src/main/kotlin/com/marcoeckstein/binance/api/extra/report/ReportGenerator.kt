package com.marcoeckstein.binance.api.extra.report

import com.marcoeckstein.binance.api.client.prvt.account.WithdrawDirection
import com.marcoeckstein.binance.api.extra.BinanceRestApiFacade
import com.marcoeckstein.binance.api.extra.extensions.assets
import java.math.BigDecimal

class ReportGenerator(
    private val facade: BinanceRestApiFacade,
) {

    fun getAssetQuantitiesReports(): Map<String, AssetQuantitiesReport> {
        val spotBalances = facade.getSpotAccountBalances()
        val isolatedMarginDetails = facade.getIsolatedMarginAccountDetails()
        val flexibleSavingsPositions = facade.getFlexibleSavingsPositions()
        val lockedStakingPositions = facade.getLockedStakingPositions()

        return facade.getAllCoinsInformation().map { it.coin }.map { asset ->
            asset to AssetQuantitiesReport(
                asset = asset,
                spotFree = spotBalances.singleOrNull { it.asset == asset }?.free ?: BigDecimal.ZERO,
                spotLocked = spotBalances.singleOrNull { it.asset == asset }?.locked ?: BigDecimal.ZERO,
                isolatedMarginFree =
                isolatedMarginDetails.flatMap { it.assets }.filter { it.assetName == asset }.sumOf { it.free },
                isolatedMarginLocked =
                isolatedMarginDetails.flatMap { it.assets }.filter { it.assetName == asset }
                    .sumOf { it.locked },
                isolatedMarginBorrowed =
                isolatedMarginDetails.flatMap { it.assets }.filter { it.assetName == asset }
                    .sumOf { it.borrowed },
                isolatedMarginInterest =
                isolatedMarginDetails.flatMap { it.assets }.filter { it.assetName == asset }
                    .sumOf { it.interest },
                flexibleSavings = flexibleSavingsPositions.filter { it.asset == asset }
                    .sumOf { it.totalAmount },
                lockedStaking = lockedStakingPositions.filter { it.asset == asset }.sumOf { it.amount },
            )
        }.toMap()
    }

    fun getAssetHistoryReports(): Map<String, AssetHistoryReport> {
        val fiatDeposits = facade.getFiatDepositAndWithdrawHistory(WithdrawDirection.DEPOSIT)
        val fiatWithdrawals = facade.getFiatDepositAndWithdrawHistory(WithdrawDirection.WITHDRAW)
        val payments = facade.getPaymentHistory()
        val trades = facade.getTradeHistory()
        val distributions = facade.getDistributionHistory()
        val flexibleSavingsInterests = facade.getFlexibleSavingsInterestHistory()
        val lockedStakingInterests = facade.getLockedStakingInterestHistory()
        val isolatedMarginBorrowings = facade.getIsolatedMarginBorrowingHistory()
        val isolatedMarginRepayments = facade.getIsolatedMarginRepaymentHistory()
        val isolatedMarginInterests = facade.getIsolatedMarginInterestHistory()
        val isolatedMarginRebates = facade.getIsolatedMarginRebateHistory()
        return facade.getAllCoinsInformation().map { it.coin }.map { asset ->
            asset to AssetHistoryReport(
                asset = asset,
                fiatDeposits = fiatDeposits,
                fiatWithdrawals = fiatWithdrawals,
                payments = payments,
                trades = trades,
                distributions = distributions,
                flexibleSavingsInterests = flexibleSavingsInterests,
                lockedStakingInterests = lockedStakingInterests,
                isolatedMarginBorrowings = isolatedMarginBorrowings,
                isolatedMarginRepayments = isolatedMarginRepayments,
                isolatedMarginInterests = isolatedMarginInterests,
                isolatedMarginRebates = isolatedMarginRebates,
            )
        }.toMap()
    }
}
