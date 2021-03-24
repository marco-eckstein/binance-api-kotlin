package com.marcoeckstein.binance.api.lib.jvm

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

internal object InstantFactory {

    // See https://stackoverflow.com/a/23886207
    fun startOfMonth(year: Int, month: Int, zoneId: ZoneId): Instant =
        LocalDate.of(year, month, 1).atStartOfDay(zoneId).toInstant()
}
