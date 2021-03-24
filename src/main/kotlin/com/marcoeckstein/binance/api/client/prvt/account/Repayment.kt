package com.marcoeckstein.binance.api.client.prvt.account

import java.math.BigDecimal

interface Repayment : Timestamped {

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

    /**
     * E.g. CONFIRM
     */
    val status: String

    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // public val clientInfo: null,
}
