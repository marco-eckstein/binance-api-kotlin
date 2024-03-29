package com.marcoeckstein.binance.api.client.prvt.account.request

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.marcoeckstein.binance.api.lib.guava.split
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

interface PeriodQuery<T : PeriodQuery<T>> {

    val startTime: Instant?

    /**
     * End time. Depending on `isEndTimeInclusive`, it is inclusive or exclusive.
     */
    val endTime: Instant?

    val periodInfo: PeriodInfo

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
                    if (periodInfo.endTimeType == BoundType.CLOSED)
                        it.minus(1, periodInfo.timestampResolution)
                    else
                        it
                }
            )
        }
    }

    companion object {

        // The exact value is unknown. It is "three months".
        // We choose a value that is a little lower for safety.
        val maxIntervalDuration: Duration = Duration.ofDays(85)
    }

    interface PeriodInfo {

        val endTimeType: BoundType
        val timestampResolution: ChronoUnit

        fun calculateStartTime(range: Range<Instant>): Instant =
            range.lowerEndpoint().let {
                if (range.lowerBoundType() == BoundType.CLOSED) it else it.plus(1, timestampResolution)
            }

        fun calculateEndTime(range: Range<Instant>): Instant =
            range.upperEndpoint().let {
                if (endTimeType == BoundType.CLOSED) {
                    if (range.upperBoundType() == BoundType.CLOSED) it else it.minus(1, timestampResolution)
                } else {
                    if (range.upperBoundType() == BoundType.OPEN) it else it.plus(1, timestampResolution)
                }
            }
    }
}

internal fun PeriodQuery<*>.requireValidPeriod() {
    startTime?.also {
        require(it == it.truncatedTo(periodInfo.timestampResolution)) {
            "startTime must have resolution $periodInfo.timestampResolution, but was$it."
        }
        endTime?.let { end ->
            require(it <= end) {
                "endTime cannot be greater than startTime, but $it > $end."
            }
        }
    }
    endTime?.also {
        val resolution = periodInfo.timestampResolution
        require(it == it.truncatedTo(resolution)) {
            "endTime must have resolution $resolution, but was $it."
        }
    }
}
