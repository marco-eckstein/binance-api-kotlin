package com.marcoeckstein.binance.api.extra.report

import com.binance.api.client.domain.OrderSide
import com.marcoeckstein.binance.api.client.prvt.account.Distribution
import com.marcoeckstein.binance.api.client.prvt.account.FiatDepositAndWithdrawHistoryEntry
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginBorrowing
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginInterest
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginRebate
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginRepayment
import com.marcoeckstein.binance.api.client.prvt.account.Payment
import com.marcoeckstein.binance.api.client.prvt.account.Trade
import com.marcoeckstein.binance.api.client.prvt.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.api.extra.extensions.cryptoCurrencyDelta
import com.marcoeckstein.binance.api.extra.extensions.fiatCurrencyDelta
import java.math.BigDecimal

data class AssetHistoryReport(
    val asset: String,
    val fiatDeposits: List<FiatDepositAndWithdrawHistoryEntry>,
    val fiatWithdrawals: List<FiatDepositAndWithdrawHistoryEntry>,
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

    val fiatBalance = fiatDeposits.filter { it.fiatCurrency == asset }.sumOf { it.indicatedAmount } -
        fiatWithdrawals.filter { it.fiatCurrency == asset }.sumOf { it.amount }

    val paymentBalance =
        payments
            .filter { it.status == "4" }
            .sumOf {
                when (asset) {
                    it.fiatCurrency -> it.fiatCurrencyDelta
                    it.cryptoCurrency -> it.cryptoCurrencyDelta
                    else -> BigDecimal.ZERO
                }
            }

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

    val fees = trades.sumOf { if (asset == it.feeAsset) it.fee else BigDecimal.ZERO } +
        (fiatDeposits + fiatWithdrawals).filter { it.fiatCurrency == asset }.sumOf { it.totalFee }

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
        fiatBalance,
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
        Fiat balance: ${fiatBalance.toPlainString()}
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
