package com.marcoeckstein.binance.prvt.api.extra.extensions

import com.binance.api.client.domain.OrderSide
import com.marcoeckstein.binance.prvt.api.client.account.Order
import com.marcoeckstein.binance.prvt.api.client.account.OrderStatus
import java.math.BigDecimal

fun Order.lockedAmount(asset: String): BigDecimal =
    when (status) {
        OrderStatus.NEW ->
            when {
                side == OrderSide.BUY && quoteAsset == asset -> delegateMoney
                side == OrderSide.SELL && baseAsset == asset -> origQty
                else -> BigDecimal.ZERO
            }
        OrderStatus.PARTIAL_FILL, OrderStatus.PENDING_CANCEL ->
            throw NotImplementedError()
        OrderStatus.FILLED, OrderStatus.CANCELED, OrderStatus.EXPIRED, OrderStatus.REJECTED ->
            BigDecimal.ZERO
    }
