package com.marcoeckstein.binance.api

import com.marcoeckstein.binance.api.lib.jvm.InstantFactory
import okhttp3.logging.HttpLoggingInterceptor
import java.nio.file.Path
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.Properties
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.reader

@ExperimentalPathApi
class Config(
    private val path: Path = Path.of("config.properties")
) {

    val apiKey: String get() = properties.getProperty("apiKey")

    val secret: String get() = properties.getProperty("secret")

    val curlAddressPosix: String get() = properties.getProperty("curlAddressPosix")

    val accountStartTime: Instant
        get() =
            InstantFactory.startOfMonth(
                year = properties.getProperty("accountStartYear").toInt(),
                month = properties.getProperty("accountStartMonth").toInt(),
                ZoneOffset.UTC
            ).truncatedTo(ChronoUnit.MILLIS)

    val logLevel: HttpLoggingInterceptor.Level
        get() = HttpLoggingInterceptor.Level.valueOf(properties.getProperty("logLevel"))

    val exportDirectory: Path
        get() = Path.of(properties.getProperty("exportDirectory"))

    private val properties: Properties by lazy {
        path.reader().use {
            Properties().apply { load(it) }
        }
    }
}
