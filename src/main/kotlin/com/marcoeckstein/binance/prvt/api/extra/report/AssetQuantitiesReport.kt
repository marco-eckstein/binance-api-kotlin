package com.marcoeckstein.binance.prvt.api.extra.report

import java.math.BigDecimal

data class AssetQuantitiesReport(
    val asset: String,
    val spotFree: BigDecimal,
    val spotLocked: BigDecimal,
    val isolatedMarginFree: BigDecimal,
    val isolatedMarginLocked: BigDecimal,
    val isolatedMarginBorrowed: BigDecimal,
    val isolatedMarginInterest: BigDecimal,
    val flexibleSavings: BigDecimal,
    val lockedStaking: BigDecimal,
) {

    val gross: BigDecimal =
        listOf(
            spotFree,
            spotLocked,
            isolatedMarginFree,
            isolatedMarginLocked,
            flexibleSavings,
            lockedStaking,
        ).sumOf { it }

    val net: BigDecimal = gross - (isolatedMarginBorrowed + isolatedMarginInterest)

    fun toReportString(): String = """
        = Quantities =
        Spot free: ${spotFree.toPlainString()}
        Spot locked: ${spotLocked.toPlainString()}
        Isolated margin free: ${isolatedMarginFree.toPlainString()}
        Isolated margin locked: ${isolatedMarginLocked.toPlainString()}
        Flexible savings: ${flexibleSavings.toPlainString()}
        Locked staking: ${lockedStaking.toPlainString()}
      """.trimIndent()
}
