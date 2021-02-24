package com.marcoeckstein.binance.prvt.api.client.account

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PaymentType.Serializer::class)
enum class PaymentType(internal val serializedName: String) {

    BUY("0"),
    SELL("1"),
    ;

    internal object Serializer : KSerializer<PaymentType> {

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor(PaymentType::class.simpleName!!, PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: PaymentType) {
            encoder.encodeString(value.serializedName)
        }

        override fun deserialize(decoder: Decoder): PaymentType {
            val serializedName = decoder.decodeString()
            return values().single { it.serializedName == serializedName }
        }
    }
}
