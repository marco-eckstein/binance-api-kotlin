package com.marcoeckstein.binance.prvt.api.extra.extensions

import com.marcoeckstein.binance.prvt.api.client.account.Payment
import com.marcoeckstein.binance.prvt.api.client.account.PaymentType
import java.math.BigDecimal

val Payment.cryptoCurrencyDelta: BigDecimal
    get() = if (type == PaymentType.BUY) obtainAmount else obtainAmount.negate()
