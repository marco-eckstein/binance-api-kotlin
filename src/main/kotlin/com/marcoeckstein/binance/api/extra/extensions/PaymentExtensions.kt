package com.marcoeckstein.binance.api.extra.extensions

import com.marcoeckstein.binance.api.client.prvt.account.Payment
import java.math.BigDecimal

val Payment.cryptoCurrencyDelta: BigDecimal
    get() = if (type == com.marcoeckstein.binance.api.client.prvt.account.PaymentType.BUY) obtainAmount else sourceAmount.negate()
