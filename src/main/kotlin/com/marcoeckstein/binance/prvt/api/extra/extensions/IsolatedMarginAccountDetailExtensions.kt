package com.marcoeckstein.binance.prvt.api.extra.extensions

import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginAccountDetail

val IsolatedMarginAccountDetail.assets: List<IsolatedMarginAccountDetail.Asset>
    get() = listOf(base, quote)
