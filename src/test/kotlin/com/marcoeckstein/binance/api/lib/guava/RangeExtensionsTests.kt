package com.marcoeckstein.binance.api.lib.guava

import com.google.common.collect.Range
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class RangeExtensionsTests {

    @Test
    fun `it works`() {
        val lowerBound = Instant.ofEpochMilli(0)
        val upperBound = Instant.ofEpochMilli(97)
        val chunks = Range.openClosed(lowerBound, upperBound).split(Duration.ofMillis(10))
        chunks.size shouldBe 10
        chunks[0] shouldBe Range.open(Instant.ofEpochMilli(0), Instant.ofEpochMilli(10))
        chunks[1] shouldBe Range.closedOpen(Instant.ofEpochMilli(10), Instant.ofEpochMilli(20))
        chunks[8] shouldBe Range.closedOpen(Instant.ofEpochMilli(80), Instant.ofEpochMilli(90))
        chunks[9] shouldBe Range.closed(Instant.ofEpochMilli(90), Instant.ofEpochMilli(97))
    }
}
