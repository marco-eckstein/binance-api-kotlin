package com.marcoeckstein.binance.prvt.api.client.account

import java.math.BigDecimal
import java.time.Instant

interface Repayment {

    val timestamp: Instant

    val txId: String

    /**
     * The repaid asset
     */
    val asset: String

    /**
     * The total amount
     */
    val amount: BigDecimal

    /**
     * The principal amount
     */
    val principal: BigDecimal

    val interest: BigDecimal

    val status: String

    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // public val clientInfo: null,
}
