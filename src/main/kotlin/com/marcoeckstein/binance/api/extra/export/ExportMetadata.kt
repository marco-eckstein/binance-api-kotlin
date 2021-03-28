@file:UseSerializers(InstantAsIsoDateTimeSerializer::class)

package com.marcoeckstein.binance.api.extra.export

import com.marcoeckstein.binance.api.lib.jvm.InstantAsIsoDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class ExportMetadata(
    val startTime: Instant,
    val endTimeExclusive: Instant,
    val filenames: List<String>,
) {

    init {
        setOf(startTime, endTimeExclusive).forEach {
            require(it == it.truncatedTo(ChronoUnit.SECONDS))
        }
    }
}
