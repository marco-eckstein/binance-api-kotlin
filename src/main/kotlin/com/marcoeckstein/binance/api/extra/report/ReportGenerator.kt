package com.marcoeckstein.binance.api.extra.report

import com.binance.api.client.BinanceApiRestClient
import com.marcoeckstein.binance.api.extra.BinanceRestApiFacade
import com.marcoeckstein.binance.api.extra.extensions.assets
import com.marcoeckstein.binance.api.extra.extensions.getAllAssetsNames
import java.math.BigDecimal

class ReportGenerator(
    private val publicApi: BinanceApiRestClient,
    private val privateApi: BinanceRestApiFacade,
) {

    fun getAssetQuantitiesReports(): Map<String, AssetQuantitiesReport> {
        val spotBalances = privateApi.getSpotAccountBalances()
        val isolatedMarginDetails = privateApi.getIsolatedMarginAccountDetails()
        val flexibleSavingsPositions = privateApi.getFlexibleSavingsPositions()
        val lockedStakingPositions = privateApi.getLockedStakingPositions()

        return publicApi.getAllAssetsNames().map { asset ->
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
        val payments = privateApi.getPaymentHistory()
        val trades = privateApi.getTradeHistory()
        val distributions = privateApi.getDistributionHistory()
        val flexibleSavingsInterests = privateApi.getFlexibleSavingsInterestHistory()
        val lockedStakingInterests = privateApi.getLockedStakingInterestHistory()
        val isolatedMarginBorrowings = privateApi.getIsolatedMarginBorrowingHistory()
        val isolatedMarginRepayments = privateApi.getIsolatedMarginRepaymentHistory()
        val isolatedMarginInterests = privateApi.getIsolatedMarginInterestHistory()
        val isolatedMarginRebates = privateApi.getIsolatedMarginRebateHistory()
        return publicApi.getAllAssetsNames().map { asset ->
            asset to AssetHistoryReport(
                asset = asset,
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
