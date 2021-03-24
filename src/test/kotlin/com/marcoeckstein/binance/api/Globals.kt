package com.marcoeckstein.binance.api

import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.BinanceApiRestClient
import com.marcoeckstein.binance.api.client.prvt.BinancePrivateApiRestClientFactory
import com.marcoeckstein.binance.api.extra.BinanceRestApiFacade
import kotlinx.serialization.ExperimentalSerializationApi

val config = com.marcoeckstein.binance.api.Config()

val publicApi: BinanceApiRestClient =
    BinanceApiClientFactory.newInstance(config.apiKey, config.secret).newRestClient()

@ExperimentalSerializationApi
val privateApi =
    BinanceRestApiFacade(
        BinancePrivateApiRestClientFactory.newInstance(config.curlAddressPosix).newRestClient(),
        config.accountStartTime
    )
