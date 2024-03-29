package com.marcoeckstein.binance.api

import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.BinanceApiRestClient
import com.marcoeckstein.binance.api.client.prvt.BinancePrivateApiRestClientFactory
import com.marcoeckstein.binance.api.client.public.BinancePublicApiRestClientFactory
import com.marcoeckstein.binance.api.extra.BinanceRestApiFacade
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
val config = Config()

@ExperimentalPathApi
val officialClient: BinanceApiRestClient =
    BinanceApiClientFactory.newInstance(config.apiKey, config.secret).newRestClient()

@ExperimentalPathApi
@ExperimentalSerializationApi
val facade =
    BinanceRestApiFacade(
        BinancePublicApiRestClientFactory.newInstance(config.apiKey, config.secret).newRestClient(),
        BinancePrivateApiRestClientFactory.newInstance(config.curlAddressPosix).newRestClient(),
        config.accountStartTime
    )
