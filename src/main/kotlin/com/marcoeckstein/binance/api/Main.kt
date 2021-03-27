package com.marcoeckstein.binance.api

import com.marcoeckstein.binance.api.client.prvt.BinancePrivateApiRestClientFactory
import com.marcoeckstein.binance.api.client.public.BinancePublicApiRestClientFactory
import com.marcoeckstein.binance.api.extra.BinanceRestApiFacade
import com.marcoeckstein.binance.api.extra.export.HistoryExportWriter
import kotlinx.serialization.ExperimentalSerializationApi
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ExperimentalSerializationApi
fun main(args: Array<String>) {
    val config = when {
        args.isEmpty() -> Config()
        args.size == 2 && args[0] == "--config" -> Config(Path.of(args[1]))
        else -> throw IllegalArgumentException()
    }
    println("Exporting to ${config.exportDirectory}")
    val publicClient =
        BinancePublicApiRestClientFactory
            .newInstance(config.apiKey, config.secret)
            .newRestClient(config.logLevel)
    val privateClient =
        BinancePrivateApiRestClientFactory
            .newInstance(config.curlAddressPosix)
            .newRestClient(config.logLevel)
    val facade = BinanceRestApiFacade(publicClient, privateClient, config.accountStartTime)
    val exporter = HistoryExportWriter(facade, config.exportDirectory)
    exporter.export(config.accountStartTime)
    println("Done.")
}
