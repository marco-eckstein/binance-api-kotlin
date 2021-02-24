package com.marcoeckstein.binance.prvt.api.client.account

import java.math.BigDecimal
import java.time.Instant

interface Transfer {

    val uid: String
    val timestamp: Instant
    val txId: String
    val asset: String
    val amount: BigDecimal
    val status: String
    val type: RollDirection
    val target: String
    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // val clientInfo: null
}
