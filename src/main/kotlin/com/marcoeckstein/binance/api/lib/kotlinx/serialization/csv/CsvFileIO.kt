package com.marcoeckstein.binance.api.lib.kotlinx.serialization.csv

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@ExperimentalSerializationApi
@ExperimentalPathApi
class CsvFileIO(
    private val csv: Csv,
    private val charset: Charset,
) {

    fun <T : Any> readItems(path: Path, serializer: KSerializer<T>): List<T> {
        require(path.exists())
        val serializedItems = path.readText(charset)
        return csv.decodeFromString(ListSerializer(serializer), serializedItems)
    }

    fun <T : Any> writeItems(path: Path, items: List<T>, serializer: KSerializer<T>) {
        require(path.exists())
        val serializedItems = csv.encodeToString(ListSerializer(serializer), items)
        path.writeText(serializedItems, charset)
    }
}
