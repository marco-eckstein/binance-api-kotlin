package com.marcoeckstein.binance.prvt.api.extra.extensions

import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginAccountPosition

val IsolatedMarginAccountPosition.assets: List<IsolatedMarginAccountPosition.Asset>
    get() = listOf(base, quote)
