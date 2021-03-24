package com.marcoeckstein.binance.api.extra.extensions

import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountPosition

val IsolatedMarginAccountPosition.assets: List<IsolatedMarginAccountPosition.Asset>
    get() = listOf(base, quote)
