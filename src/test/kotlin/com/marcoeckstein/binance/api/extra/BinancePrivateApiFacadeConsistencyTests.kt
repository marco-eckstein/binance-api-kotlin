package com.marcoeckstein.binance.api.extra

import com.binance.api.client.domain.account.AssetBalance
import com.marcoeckstein.binance.api.client.prvt.account.AccountType
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountDetail
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountPosition
import com.marcoeckstein.binance.api.client.prvt.account.Order
import com.marcoeckstein.binance.api.client.prvt.account.Trade
import com.marcoeckstein.binance.api.extra.extensions.assets
import com.marcoeckstein.binance.api.extra.extensions.lockedAmount
import com.marcoeckstein.binance.api.facade
import com.marcoeckstein.binance.api.officialClient
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExperimentalSerializationApi
class BinancePrivateApiFacadeConsistencyTests {

    companion object {

        @JvmStatic
        val assets: List<String> by lazy { facade.getAllCoinsInformation().map { it.coin } }

        @JvmStatic
        val tradesFromPublicApi: List<com.binance.api.client.domain.account.Trade> by lazy {
            trades.map { it.symbol }.distinct().flatMap { officialClient.getMyTrades(it) }
        }

        val spotAccountBalances: List<AssetBalance> by lazy { officialClient.getAccount().balances }

        val isolatedMarginAccountDetails: List<IsolatedMarginAccountDetail> by lazy {
            facade.getIsolatedMarginAccountDetails()
        }

        val isolatedMarginAccountPositions: List<IsolatedMarginAccountPosition> by lazy {
            facade.getIsolatedMarginAccountPositions()
        }

        val openOrdersSpot: List<Order> by lazy { facade.getOpenOrders(AccountType.SPOT) }

        val openOrdersIsolatedMargin: List<Order> by lazy {
            facade.getOpenOrders(AccountType.ISOLATED_MARGIN)
        }

        val trades: List<Trade> by lazy { facade.getTradeHistory() }
    }

    @ParameterizedTest
    @MethodSource("getAssets")
    fun `locked amount in spot account equals amount in open spot orders`(asset: String) {
        val lockedInAccount = spotAccountBalances.single { it.asset == asset }.locked.let { BigDecimal(it) }
        val lockedInOpenOrders = openOrdersSpot.sumOf { it.lockedAmount(asset) }
        lockedInAccount shouldBeEqualComparingTo lockedInOpenOrders
    }

    @ParameterizedTest
    @MethodSource("getAssets")
    fun `locked amount in isolated margin account equals amount in open isolated margin orders`(asset: String) {
        val lockedInAccount = isolatedMarginAccountDetails
            .flatMap { listOf(it.base, it.quote) }
            .filter { it.assetName == asset }
            .sumOf { it.locked }
        val lockedInOpenOrders = openOrdersIsolatedMargin.sumOf { it.lockedAmount(asset) }
        lockedInAccount shouldBeEqualComparingTo lockedInOpenOrders
    }

    @ParameterizedTest
    @MethodSource("getAssets")
    fun `quantities in isolated margin details and positions are consistent`(asset: String) {
        val inDetails = isolatedMarginAccountDetails
            .flatMap { it.assets }
            .filter { it.assetName == asset }
            .sumOf { it.netAsset }
        val inPositions =
            isolatedMarginAccountPositions
                .flatMap { it.assets }
                .filter { it.assetName == asset }
                .sumOf { it.position ?: BigDecimal.ZERO }
        inDetails shouldBeEqualComparingTo inPositions
    }

    @ParameterizedTest
    @MethodSource("getTradesFromPublicApi")
    fun `public and private API trades are consistent`(t2: com.binance.api.client.domain.account.Trade) {
        val t1 = trades.single { it.tradeId == t2.id }
        t1.tradeId shouldBe t2.id
        t1.symbol shouldBe t2.symbol
        t1.price shouldBeEqualComparingTo BigDecimal(t2.price)
        t1.qty shouldBeEqualComparingTo BigDecimal(t2.qty)
        t1.totalQuota shouldBeEqualComparingTo BigDecimal(t2.quoteQty)
        t1.fee shouldBeEqualComparingTo BigDecimal(t2.commission)
        t1.feeAsset shouldBe t2.commissionAsset
        t1.timestamp shouldBe Instant.ofEpochMilli(t2.time).truncatedTo(ChronoUnit.SECONDS)
    }
}
