package com.marcoeckstein.binance.api.extra.extensions

import com.marcoeckstein.binance.api.client.prvt.account.Payment
import com.marcoeckstein.binance.api.client.prvt.account.PaymentType
import java.math.BigDecimal

val Payment.cryptoCurrencyDelta: BigDecimal
    get() = if (type == PaymentType.BUY) obtainAmount else sourceAmount.negate()

val Payment.fiatCurrencyDelta: BigDecimal
    get() = if (type == PaymentType.SELL) obtainAmount else sourceAmount.negate()
