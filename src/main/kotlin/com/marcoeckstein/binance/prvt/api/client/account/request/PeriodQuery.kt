package com.marcoeckstein.binance.prvt.api.client.account.request

import com.google.common.collect.Range
import com.marcoeckstein.binance.prvt.api.lib.guava.split
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

interface PeriodQuery<T : PeriodQuery<T>> {

    val startTime: Instant?

    /**
     * End time. Depending on `isEndTimeInclusive`, it is inclusive or exclusive.
     */
    val endTime: Instant?

    val isEndTimeInclusive: Boolean

    val timestampResolution: ChronoUnit get() = ChronoUnit.MILLIS

    fun copyWith(startTime: Instant?, endTime: Instant?): T

    fun splitPeriod(): List<T> {
        val ranges = Range.closedOpen(
            checkNotNull(startTime),
            checkNotNull(endTime)
        ).split(maxIntervalDuration)
        return ranges.map { range ->
            copyWith(
                startTime = range.lowerEndpoint(),
                endTime = range.upperEndpoint().let {
                    if (isEndTimeInclusive) it.minus(1, timestampResolution) else it
                }
            )
        }
    }

    companion object {

        // The exact value is unknown. It is "three months".
        // We choose a value that is a little lower for safety.
        val maxIntervalDuration: Duration = Duration.ofDays(85)
    }
}

internal fun PeriodQuery<*>.requireValidPeriod() {
    startTime?.also {
        require(it == it.truncatedTo(timestampResolution)) {
            "startTime must have resolution $timestampResolution, but was$it."
        }
        endTime?.let { end ->
            require(it <= end) {
                "endTime cannot be greater than startTime, but $it > $end."
            }
        }
    }
    endTime?.also {
        val resolution = timestampResolution
        require(it == it.truncatedTo(resolution)) {
            "endTime must have resolution $resolution, but was $it."
        }
    }
}
