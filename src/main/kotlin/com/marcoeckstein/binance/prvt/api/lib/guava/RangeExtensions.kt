package com.marcoeckstein.binance.prvt.api.lib.guava

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import java.time.Duration
import java.time.Instant

fun Range<Instant>.split(maxChunkDuration: Duration): List<Range<Instant>> {
    val chunks = mutableListOf<Range<Instant>>()
    var chunkStartTime = this.lowerEndpoint()
    while (chunkStartTime < this.upperEndpoint()) {
        val chunkEndTime = listOf(chunkStartTime + maxChunkDuration, this.upperEndpoint()).minOrNull()!!
        val chunkLowerBoundType =
            if (chunkStartTime == this.lowerEndpoint()) this.lowerBoundType() else BoundType.CLOSED
        val chunkUpperBoundType =
            if (chunkEndTime == this.upperEndpoint()) this.upperBoundType() else BoundType.OPEN
        chunks += Range.range(chunkStartTime, chunkLowerBoundType, chunkEndTime, chunkUpperBoundType)
        chunkStartTime = chunkEndTime
    }
    return chunks
}
