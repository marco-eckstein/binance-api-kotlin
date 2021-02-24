package com.marcoeckstein.binance.prvt.api.client.account.request

import com.google.common.collect.Range
import com.marcoeckstein.binance.prvt.api.lib.guava.split
import java.time.Duration
import java.time.Instant

interface PeriodQuery<T : PeriodQuery<T>> {

    val startTime: Instant?
    val endTime: Instant?

    fun copyWith(startTime: Instant?, endTime: Instant?): T

    fun splitPeriod(): List<T> {
        val ranges = Range.closedOpen(
            checkNotNull(startTime),
            checkNotNull(endTime)
        ).split(maxIntervalDuration)
        return ranges.map {
            copyWith(startTime = it.lowerEndpoint(), endTime = it.upperEndpoint())
        }
    }

    companion object {

        // The exact value is unknown. It is "three months".
        // We choose a value that is a little lower for safety.
        val maxIntervalDuration: Duration = Duration.ofDays(85)
    }
}
