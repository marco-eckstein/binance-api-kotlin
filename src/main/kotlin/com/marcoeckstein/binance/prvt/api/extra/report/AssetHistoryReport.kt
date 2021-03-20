package com.marcoeckstein.binance.prvt.api.extra.report

import com.binance.api.client.domain.OrderSide
import com.marcoeckstein.binance.prvt.api.client.account.Distribution
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginBorrowing
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginInterest
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginRebate
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginRepayment
import com.marcoeckstein.binance.prvt.api.client.account.Payment
import com.marcoeckstein.binance.prvt.api.client.account.Trade
import com.marcoeckstein.binance.prvt.api.client.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.prvt.api.client.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.prvt.api.extra.extensions.cryptoCurrencyDelta
import java.math.BigDecimal

data class AssetHistoryReport(
    val asset: String,
    val payments: List<Payment>,
    val trades: List<Trade>,
    val distributions: List<Distribution>,
    val flexibleSavingsInterests: List<FlexibleSavingsInterest>,
    val lockedStakingInterests: List<LockedStakingInterest>,
    val isolatedMarginBorrowings: List<IsolatedMarginBorrowing>,
    val isolatedMarginRepayments: List<IsolatedMarginRepayment>,
    val isolatedMarginInterests: List<IsolatedMarginInterest>,
    val isolatedMarginRebates: List<IsolatedMarginRebate>,
) {

    val paymentBalance =
        payments.filter { it.status == "4" && it.cryptoCurrency == asset }.sumOf { it.cryptoCurrencyDelta }

    val tradeBalance = trades.sumOf {
        when (asset) {
            it.baseAsset -> if (it.side == OrderSide.BUY) it.qty else it.qty.negate()
            it.quoteAsset -> if (it.side == OrderSide.SELL) it.totalQuota else it.totalQuota.negate()
            else -> BigDecimal.ZERO
        }
    }

    val distributionsTotal = distributions.filter { it.asset == asset }.sumOf { it.amount }

    val flexibleSavingsInterestsTotal =
        flexibleSavingsInterests.filter { it.asset == asset }.sumOf { it.amount }

    val lockedStakingInterestsTotal =
        lockedStakingInterests.filter { it.asset == asset }.sumOf { it.interest }

    val fees = trades.sumOf { if (asset == it.feeAsset) it.fee else BigDecimal.ZERO }

    val isolatedMarginRebatesTotal =
        isolatedMarginRebates.filter { it.asset == asset }.sumOf { BigDecimal.valueOf(it.rebateAmount) }

    val isolatedMarginRebatesDeductedTotal: BigDecimal =
        if (asset == "BNB")
            isolatedMarginRebates.sumOf { BigDecimal.valueOf(it.deductBnbAmt) }
        else
            BigDecimal.ZERO

    val feeBalance = isolatedMarginRebatesTotal - fees - isolatedMarginRebatesDeductedTotal

    val isolatedMarginBorrowed =
        isolatedMarginBorrowings.filter { it.status == "CONFIRM" && it.asset == asset }.sumOf { it.principal }

    val isolatedMarginRepaid =
        isolatedMarginRepayments.filter { it.status == "CONFIRM" && it.asset == asset }.sumOf { it.principal }

    val isolatedMarginRepaidInterest =
        isolatedMarginRepayments.filter { it.status == "CONFIRM" && it.asset == asset }.sumOf { it.interest }

    val isolatedMarginBorrowBalance =
        isolatedMarginBorrowed - isolatedMarginRepaid - isolatedMarginRepaidInterest

    val isolatedMarginInterestsTotal =
        isolatedMarginInterests.filter { it.asset == asset }.sumOf { it.interest }

    val gross = listOf(
        paymentBalance,
        tradeBalance,
        distributionsTotal,
        flexibleSavingsInterestsTotal,
        lockedStakingInterestsTotal,
        feeBalance,
        isolatedMarginBorrowBalance,
    ).sumOf { it }

    val net = gross - listOf(
        isolatedMarginBorrowed,
        isolatedMarginInterestsTotal,
        isolatedMarginRepaid.negate(),
        isolatedMarginRepaidInterest.negate()
    ).sumOf { it }

    fun toReportString(): String = """
        = History =
        Payment balance (Buy crypto balance): ${paymentBalance.toPlainString()}
        Trade balance: ${tradeBalance.toPlainString()}
        Distribution: ${distributionsTotal.toPlainString()}
        Earned flexible savings interest: ${flexibleSavingsInterestsTotal.toPlainString()}
        Earned locked staking interests: ${lockedStakingInterestsTotal.toPlainString()}
        Fee balance : ${feeBalance.toPlainString()}
            Fees: ${fees.toPlainString()}
            Rebates/deducted: ${isolatedMarginRebatesDeductedTotal.toPlainString()}
            Rebates: ${isolatedMarginRebatesTotal.toPlainString()}
        Borrow balance: ${isolatedMarginBorrowBalance.toPlainString()}
            Borrowed: ${isolatedMarginBorrowed.toPlainString()}
            Repaid: ${isolatedMarginRepaid.toPlainString()}
            Repaid interest: ${isolatedMarginRepaidInterest.toPlainString()}
        Interest: ${isolatedMarginInterestsTotal.toPlainString()}
      """.trimIndent()
}
