package com.marcoeckstein.binance.prvt.api.extra.extensions

import com.binance.api.client.domain.OrderSide
import com.marcoeckstein.binance.prvt.api.client.account.Trade
import java.math.BigDecimal

val Trade.baseAssetDelta: BigDecimal
    get() =
        if (side == OrderSide.BUY)
            qty - (if (feeAsset == baseAsset) fee else BigDecimal.ZERO)
        else
            qty.negate()

val Trade.quoteAssetDelta: BigDecimal
    get() =
        if (side == OrderSide.SELL)
            totalQuota - (if (feeAsset == quoteAsset) fee else BigDecimal.ZERO)
        else
            totalQuota.negate()
