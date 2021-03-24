package com.marcoeckstein.binance.api.extra.extensions

import com.binance.api.client.domain.OrderSide
import com.marcoeckstein.binance.api.client.prvt.account.Order
import java.math.BigDecimal

fun Order.lockedAmount(asset: String): BigDecimal =
    when (status) {
        com.marcoeckstein.binance.api.client.prvt.account.OrderStatus.NEW ->
            when {
                side == OrderSide.BUY && quoteAsset == asset -> delegateMoney
                side == OrderSide.SELL && baseAsset == asset -> origQty
                else -> BigDecimal.ZERO
            }
        com.marcoeckstein.binance.api.client.prvt.account.OrderStatus.PARTIAL_FILL, com.marcoeckstein.binance.api.client.prvt.account.OrderStatus.PENDING_CANCEL ->
            throw NotImplementedError()
        com.marcoeckstein.binance.api.client.prvt.account.OrderStatus.FILLED, com.marcoeckstein.binance.api.client.prvt.account.OrderStatus.CANCELED, com.marcoeckstein.binance.api.client.prvt.account.OrderStatus.EXPIRED, com.marcoeckstein.binance.api.client.prvt.account.OrderStatus.REJECTED ->
            BigDecimal.ZERO
    }
