package com.marcoeckstein.binance.prvt.api.lib.jvm

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

internal object InstantAsDateSerializer : KSerializer<Instant> {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(InstantAsDateSerializer::class.simpleName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(
            formatter.format(value.atOffset(ZoneOffset.UTC))
        )
    }

    override fun deserialize(decoder: Decoder): Instant =
        LocalDateTime.parse(decoder.decodeString(), formatter).atOffset(ZoneOffset.UTC).toInstant()
}
