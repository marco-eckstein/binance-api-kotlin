package com.marcoeckstein.binance.prvt.api.client.account

import java.math.BigDecimal
import java.time.Instant

interface PaidInterest {

    val txId: String

    val chargeEpoch: Instant

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

    val status: String?
}
