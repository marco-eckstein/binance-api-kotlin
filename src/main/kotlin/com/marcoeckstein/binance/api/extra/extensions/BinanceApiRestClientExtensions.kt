package com.marcoeckstein.binance.api.extra.extensions

import com.binance.api.client.BinanceApiRestClient

// getAllAssets() does not work.
fun BinanceApiRestClient.getAllAssetsNames(): List<String> =
    getExchangeInfo().symbols.flatMap { listOf(it.baseAsset, it.quoteAsset) }.distinct().sorted()
