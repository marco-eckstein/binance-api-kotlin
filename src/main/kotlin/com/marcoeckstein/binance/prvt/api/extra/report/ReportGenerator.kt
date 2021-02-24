package com.marcoeckstein.binance.prvt.api.extra.report

import com.binance.api.client.BinanceApiRestClient
import com.marcoeckstein.binance.prvt.api.client.BinancePrivateApiRestClient
import com.marcoeckstein.binance.prvt.api.extra.extensions.assets
import com.marcoeckstein.binance.prvt.api.extra.extensions.getAllAssetsNames
import com.marcoeckstein.binance.prvt.api.extra.extensions.getDistributionHistory
import com.marcoeckstein.binance.prvt.api.extra.extensions.getFlexibleSavingsInterestHistory
import com.marcoeckstein.binance.prvt.api.extra.extensions.getFlexibleSavingsPositions
import com.marcoeckstein.binance.prvt.api.extra.extensions.getIsolatedMarginBorrowingHistory
import com.marcoeckstein.binance.prvt.api.extra.extensions.getIsolatedMarginInterestHistory
import com.marcoeckstein.binance.prvt.api.extra.extensions.getIsolatedMarginRebateHistory
import com.marcoeckstein.binance.prvt.api.extra.extensions.getIsolatedMarginRepaymentHistory
import com.marcoeckstein.binance.prvt.api.extra.extensions.getLockedStakingInterestHistory
import com.marcoeckstein.binance.prvt.api.extra.extensions.getLockedStakingPositions
import com.marcoeckstein.binance.prvt.api.extra.extensions.getPaymentHistory
import com.marcoeckstein.binance.prvt.api.extra.extensions.getSpotAccountBalances
import com.marcoeckstein.binance.prvt.api.extra.extensions.getTradeHistory
import java.math.BigDecimal
import java.time.Instant

class ReportGenerator(
    private val publicClient: BinanceApiRestClient,
    private val privateClient: BinancePrivateApiRestClient,
) {

    fun getAssetQuantitiesReports(): Map<String, AssetQuantitiesReport> {
        val spotBalances = privateClient.getSpotAccountBalances()
        val isolatedMarginDetails = privateClient.getIsolatedMarginAccountDetails()
        val flexibleSavingsPositions = privateClient.getFlexibleSavingsPositions()
        val lockedStakingPositions = privateClient.getLockedStakingPositions()

        return publicClient.getAllAssetsNames().map { asset ->
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

    fun getAssetHistoryReport(start: Instant): Map<String, AssetHistoryReport> {
        val payments = privateClient.getPaymentHistory()
        val trades = privateClient.getTradeHistory(start)
        val distributions = privateClient.getDistributionHistory(start)
        val flexibleSavingsInterests = privateClient.getFlexibleSavingsInterestHistory(start)
        val lockedStakingInterests = privateClient.getLockedStakingInterestHistory(start)
        val isolatedMarginBorrowings = privateClient.getIsolatedMarginBorrowingHistory(start)
        val isolatedMarginRepayments = privateClient.getIsolatedMarginRepaymentHistory(start)
        val isolatedMarginInterests = privateClient.getIsolatedMarginInterestHistory(start)
        val isolatedMarginRebates = privateClient.getIsolatedMarginRebateHistory(start)
        return publicClient.getAllAssetsNames().map { asset ->
            asset to AssetHistoryReport(
                asset = asset,
                payments = payments.filter { it.cryptoCurrency == asset },
                trades = trades.filter { it.baseAsset == asset || it.quoteAsset == asset },
                distributions = distributions.filter { it.asset == asset },
                flexibleSavingsInterests = flexibleSavingsInterests.filter { it.asset == asset },
                lockedStakingInterests = lockedStakingInterests.filter { it.asset == asset },
                isolatedMarginBorrowings = isolatedMarginBorrowings.filter { it.asset == asset },
                isolatedMarginRepayments = isolatedMarginRepayments.filter { it.asset == asset },
                isolatedMarginInterests = isolatedMarginInterests.filter { it.asset == asset },
                isolatedMarginRebates = isolatedMarginRebates.filter { it.asset == asset },
            )
        }.toMap()
    }
}
