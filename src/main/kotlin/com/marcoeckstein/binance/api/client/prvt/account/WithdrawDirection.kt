package com.marcoeckstein.binance.api.client.prvt.account

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = WithdrawDirection.Serializer::class)
enum class WithdrawDirection(internal val serializedName: String) {

    DEPOSIT("deposit"),
    WITHDRAW("withdraw"),
    ;

    internal object Serializer : KSerializer<WithdrawDirection> {

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor(WithdrawDirection::class.simpleName!!, PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: WithdrawDirection) {
            encoder.encodeString(value.serializedName)
        }

        override fun deserialize(decoder: Decoder): WithdrawDirection {
            val serializedName = decoder.decodeString()
            return values().single { it.serializedName == serializedName }
        }
    }
}
