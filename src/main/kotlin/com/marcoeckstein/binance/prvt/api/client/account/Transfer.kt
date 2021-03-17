package com.marcoeckstein.binance.prvt.api.client.account

import java.math.BigDecimal

interface Transfer : Timestamped {

    val txId: String
    val uid: String
    val asset: String
    val amount: BigDecimal
    val status: String
    val type: RollDirection
    val target: String
    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // val clientInfo: null
}
