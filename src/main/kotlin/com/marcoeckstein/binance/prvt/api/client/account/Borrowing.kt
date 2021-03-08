package com.marcoeckstein.binance.prvt.api.client.account

import java.math.BigDecimal

interface Borrowing : Timestamped {

    val txId: String

    /**
     * The borrowed asset
     */
    val asset: String

    /**
     * The amount
     */
    val principal: BigDecimal

    val status: String

    val fromId: String?

    val toId: String?

    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // public val clientInfo: null,
}
