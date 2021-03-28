package com.marcoeckstein.binance.api.extra.export

import com.marcoeckstein.binance.api.lib.jvm.InstantAsIsoDateTimeSerializer
import com.marcoeckstein.binance.api.lib.kotlinx.serialization.csv.CsvFileIO
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.nio.charset.Charset
import kotlin.io.path.ExperimentalPathApi

internal const val MetadataFileName = "export-metadata.json"
internal val charset: Charset = Charset.forName("UTF-8")

@ExperimentalSerializationApi
@ExperimentalPathApi
internal fun csvFileIO(): CsvFileIO {
    val csv = Csv(Csv.Rfc4180) {
        serializersModule = SerializersModule {
            contextual(InstantAsIsoDateTimeSerializer)
            hasHeaderRecord = true
        }
    }
    return CsvFileIO(csv, charset)
}
