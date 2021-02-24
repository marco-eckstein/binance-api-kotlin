package com.marcoeckstein.binance.prvt.api

import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantFactory
import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.Properties

class Config {

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

    private val properties: Properties by lazy {
        File("config.properties").reader().use {
            Properties().apply { load(it) }
        }
    }
}
