package com.marcoeckstein.binance.prvt.api.extra.report

import com.marcoeckstein.binance.prvt.api.client.account.Distribution
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginBorrowing
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginInterest
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginRebate
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginRepayment
import com.marcoeckstein.binance.prvt.api.client.account.Payment
import com.marcoeckstein.binance.prvt.api.client.account.Trade
import com.marcoeckstein.binance.prvt.api.client.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.prvt.api.client.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.prvt.api.extra.extensions.baseAssetDelta
import com.marcoeckstein.binance.prvt.api.extra.extensions.cryptoCurrencyDelta
import com.marcoeckstein.binance.prvt.api.extra.extensions.quoteAssetDelta
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
        payments.sumOf { it.cryptoCurrencyDelta }

    val tradeBalance =
        (trades.groupBy { it.baseAsset }.mapValues { entry -> entry.value.sumOf { it.baseAssetDelta } }[asset]
            ?: BigDecimal.ZERO) +
            (trades.groupBy { it.quoteAsset }
                .mapValues { entry -> entry.value.sumOf { it.quoteAssetDelta } }[asset] ?: BigDecimal.ZERO)

    val distributionsTotal = distributions.sumOf { it.amount }

    val flexibleSavingsInterestsTotal = flexibleSavingsInterests.sumOf { it.amount }

    val lockedStakingInterestsTotal = lockedStakingInterests.sumOf { it.interest }

    val isolatedMarginRebatesTotal = isolatedMarginRebates.sumOf { BigDecimal.valueOf(it.rebateAmount) }

    val isolatedMarginBorrowed = isolatedMarginBorrowings.sumOf { it.principal }

    val isolatedMarginRepaid = isolatedMarginRepayments.sumOf { it.principal }

    val isolatedMarginRepaidInterest = isolatedMarginRepayments.sumOf { it.interest }

    val isolatedMarginBorrowBalance =
        isolatedMarginBorrowed - isolatedMarginRepaid - isolatedMarginRepaidInterest

    val isolatedMarginInterestsTotal = isolatedMarginInterests.sumOf { it.interest }

    val gross = listOf(
        paymentBalance,
        tradeBalance,
        distributionsTotal,
        flexibleSavingsInterestsTotal,
        lockedStakingInterestsTotal,
        isolatedMarginRebatesTotal,
        isolatedMarginBorrowBalance,
    ).sumOf { it }

    val net = gross - listOf(
        isolatedMarginBorrowed,
        isolatedMarginInterestsTotal,
        isolatedMarginRepaid.negate(),
        isolatedMarginRepaidInterest.negate()
    ).sumOf { it }

    fun toReportString(): String = """
        Payment balance (Buy crypto balance): ${paymentBalance.toPlainString()}
        Trade balance: ${tradeBalance.toPlainString()}
        Distribution: ${distributionsTotal.toPlainString()}
        Earned flexible savings interest: ${flexibleSavingsInterestsTotal.toPlainString()}
        Earned locked staking interests: ${lockedStakingInterestsTotal.toPlainString()}
        Rebates: ${isolatedMarginRebatesTotal.toPlainString()}
        Borrow balance: ${isolatedMarginBorrowBalance.toPlainString()}
            Borrowed: ${isolatedMarginBorrowed.toPlainString()}
            Repaid: ${isolatedMarginRepaid.toPlainString()}
            Repaid interest: ${isolatedMarginRepaidInterest.toPlainString()}
        Interest: ${isolatedMarginInterestsTotal.toPlainString()}
      """.trimIndent()
}
