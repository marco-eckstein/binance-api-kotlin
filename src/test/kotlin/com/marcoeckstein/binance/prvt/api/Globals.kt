package com.marcoeckstein.binance.prvt.api

import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.BinanceApiRestClient
import com.marcoeckstein.binance.prvt.api.client.BinancePrivateApiRestClientFactory
import com.marcoeckstein.binance.prvt.api.extra.BinancePrivateApiFacade
import kotlinx.serialization.ExperimentalSerializationApi

val config = Config()

val publicApi: BinanceApiRestClient =
    BinanceApiClientFactory.newInstance(config.apiKey, config.secret).newRestClient()

@ExperimentalSerializationApi
val privateApi =
    BinancePrivateApiFacade(
        BinancePrivateApiRestClientFactory.newInstance(config.curlAddressPosix).newRestClient()
    )
