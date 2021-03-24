package com.marcoeckstein.binance.api.client.prvt.account

import java.math.BigDecimal

interface PaidInterest : Timestamped {

    val txId: String

    val asset: String

    /**
     * The principal amount
     */
    val principal: BigDecimal

    val interest: BigDecimal

    val type: String

    /**
     * Daily interest rate
     */
    val interestRate: BigDecimal
}
