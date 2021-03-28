package com.marcoeckstein.binance.api.extra.export

import com.google.common.collect.Range
import com.marcoeckstein.binance.api.client.prvt.account.Timestamped
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.api.extra.BinanceRestApiFacade
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@ExperimentalPathApi
@ExperimentalSerializationApi
class HistoryExportWriter(
    private val api: BinanceRestApiFacade,
    private val directory: Path = Path.of("."),
) {

    fun export(startTime: Instant) {
        val json = Json { prettyPrint = true }
        if (!directory.exists())
            directory.createDirectory()
        val metadataPath = directory.resolve(MetadataFileName)
        val metadata =
            if (metadataPath.exists()) {
                json.decodeFromString(ExportMetadata.serializer(), metadataPath.readText())
            } else {
                metadataPath.createFile()
                ExportMetadata(
                    startTime = startTime,
                    endTimeExclusive = startTime,
                    filenames = listOf()
                )
            }
        if (startTime != metadata.startTime)
            throw IllegalArgumentException(
                "Parameter startTime is $startTime, but startTime in export metadata is ${metadata.startTime}."
            )
        val nextMetadata = export(metadata)
        metadataPath.writeText(json.encodeToString(nextMetadata), charset)
    }

    private fun export(exportMetadata: ExportMetadata): ExportMetadata {
        val nextEndTimeExclusive = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        itemInfos
            .map {
                readItemsFromApi(it, exportMetadata, nextEndTimeExclusive)
            }
            .forEach {
                // Note that this happens after all items for each itemInfo have been read.
                // This is important to prevent inconsistency in case of an error while reading items.
                // For now, we do not handle errors while writing files.
                appendItemsToFile(it, exportMetadata)
            }
        return exportMetadata.copy(
            endTimeExclusive = nextEndTimeExclusive,
            filenames = itemInfos.map { it.filename }
        )
    }

    private fun <T : Any> readItemsFromApi(
        itemInfo: ItemInfo<T>,
        exportMetadata: ExportMetadata,
        nextEndTimeExclusive: Instant
    ): ItemsContainer<T> {
        val hasBeenExportedBefore = itemInfo.filename in exportMetadata.filenames
        val startTime = if (hasBeenExportedBefore) exportMetadata.endTimeExclusive else exportMetadata.startTime
        val items = itemInfo.getItems(api, Range.closedOpen(startTime, nextEndTimeExclusive))
        return ItemsContainer(items, itemInfo)
    }

    // Need to jump through hoops because of generics.
    private data class ItemsContainer<T : Any>(
        val items: List<T>,
        val itemInfo: ItemInfo<T>,
    )

    private fun <T : Any> appendItemsToFile(params: ItemsContainer<T>, exportMetadata: ExportMetadata) =
        appendItemsToFile(params.items, params.itemInfo, exportMetadata)

    private fun <T : Any> appendItemsToFile(
        items: List<T>,
        itemInfo: ItemInfo<T>,
        exportMetadata: ExportMetadata
    ) {
        val hasBeenExportedBefore = itemInfo.filename in exportMetadata.filenames
        val path = directory.resolve(itemInfo.filename).also {
            if (hasBeenExportedBefore) {
                check(it.exists())
            } else {
                check(!it.exists())
                it.createFile()
            }
        }
        val csvFileIO = csvFileIO()
        val existingItems = csvFileIO.readItems(path, itemInfo.serializer)
        val allItems = existingItems + items
        validateItems(allItems)
        csvFileIO.writeItems(path, allItems, itemInfo.serializer)
    }

    @Suppress("TooGenericExceptionThrown")
    private fun validateItems(items: List<*>) {
        items.filter { it !is LockedStakingInterest }.also {
            if (it.size != it.distinct().size) {
                throw RuntimeException("There are duplicate items.")
            }
        }
        items.filterIsInstance<Timestamped>().also {
            if (it != it.sortedBy { it.timestamp }) {
                throw RuntimeException("The items are not sorted by timestamp.")
            }
        }
    }
}
