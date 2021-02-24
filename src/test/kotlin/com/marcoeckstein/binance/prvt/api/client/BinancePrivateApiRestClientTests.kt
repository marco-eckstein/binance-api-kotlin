package com.marcoeckstein.binance.prvt.api.client

import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.domain.account.AssetBalance
import com.marcoeckstein.binance.prvt.api.Config
import com.marcoeckstein.binance.prvt.api.client.account.AccountType
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginAccountDetail
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginAccountPosition
import com.marcoeckstein.binance.prvt.api.client.account.Order
import com.marcoeckstein.binance.prvt.api.client.account.Trade
import com.marcoeckstein.binance.prvt.api.extra.extensions.assets
import com.marcoeckstein.binance.prvt.api.extra.extensions.getAllAssetsNames
import com.marcoeckstein.binance.prvt.api.extra.extensions.getTradeHistory
import com.marcoeckstein.binance.prvt.api.extra.extensions.lockedAmount
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExperimentalSerializationApi
class BinancePrivateApiRestClientTests {

    companion object {

        private val config = Config()

        private val publicClient =
            BinanceApiClientFactory.newInstance(config.apiKey, config.secret).newRestClient()

        private val privateClient =
            BinancePrivateApiRestClientFactory.newInstance(config.curlAddressPosix).newRestClient()

        @JvmStatic
        val assets: List<String> by lazy { publicClient.getAllAssetsNames() }

        @JvmStatic
        val tradesFromPublicApi: List<com.binance.api.client.domain.account.Trade> by lazy {
            trades.map { it.symbol }.distinct().flatMap { publicClient.getMyTrades(it) }
        }

        val spotAccountBalances: List<AssetBalance> by lazy { publicClient.getAccount().balances }

        val isolatedMarginAccountDetails: List<IsolatedMarginAccountDetail> by lazy {
            privateClient.getIsolatedMarginAccountDetails()
        }

        val isolatedMarginAccountPositions: List<IsolatedMarginAccountPosition> by lazy {
            privateClient.getIsolatedMarginAccountPositions()
        }

        val openOrdersSpot: List<Order> by lazy { privateClient.getOpenOrders(AccountType.SPOT) }

        val openOrdersIsolatedMargin: List<Order> by lazy {
            privateClient.getOpenOrders(AccountType.ISOLATED_MARGIN)
        }

        val trades: List<Trade> by lazy { privateClient.getTradeHistory(config.accountStartTime) }
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
        t1.time shouldBe Instant.ofEpochMilli(t2.time).truncatedTo(ChronoUnit.SECONDS)
    }
}
