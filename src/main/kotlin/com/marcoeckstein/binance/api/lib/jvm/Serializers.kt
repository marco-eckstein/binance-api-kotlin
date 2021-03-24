package com.marcoeckstein.binance.api.lib.jvm

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

internal object BigDecimalAsPlainStringSerializer : KSerializer<BigDecimal> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(BigDecimalAsPlainStringSerializer::class.simpleName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toPlainString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal =
        BigDecimal(decoder.decodeString())
}

internal object InstantAsEpochMilliSerializer : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(InstantAsEpochMilliSerializer::class.simpleName!!, PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        require(value == value.truncatedTo(ChronoUnit.MILLIS)) { "Value must not be more precise than millis." }
        encoder.encodeLong(value.toEpochMilli())
    }

    override fun deserialize(decoder: Decoder): Instant =
        Instant.ofEpochMilli(decoder.decodeLong())
}

internal object InstantAsEpochSecondSerializer : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(InstantAsEpochSecondSerializer::class.simpleName!!, PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        require(value == value.truncatedTo(ChronoUnit.SECONDS)) {
            "Value must not be more precise than seconds."
        }
        encoder.encodeLong(value.epochSecond)
    }

    override fun deserialize(decoder: Decoder): Instant =
        Instant.ofEpochSecond(decoder.decodeLong())
}

internal object InstantAsIsoDateTimeSerializer :
    InstantAsDateTimeSerializerBase(DateTimeFormatter.ISO_DATE_TIME, ChronoUnit.NANOS)

internal abstract class InstantAsDateTimeSerializerBase(
    private val formatter: DateTimeFormatter,
    private val maxPrecision: ChronoUnit,
) : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(javaClass.simpleName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        require(value == value.truncatedTo(maxPrecision)) {
            "Value must not be more precise than $maxPrecision."
        }
        encoder.encodeString(
            formatter.format(value.atOffset(ZoneOffset.UTC))
        )
    }

    override fun deserialize(decoder: Decoder): Instant =
        LocalDateTime.parse(decoder.decodeString(), formatter).atOffset(ZoneOffset.UTC).toInstant()
}
