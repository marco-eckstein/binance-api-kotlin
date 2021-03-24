package com.marcoeckstein.binance.api.extra.extensions

import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountDetail

val IsolatedMarginAccountDetail.assets: List<IsolatedMarginAccountDetail.Asset>
    get() = listOf(base, quote)
