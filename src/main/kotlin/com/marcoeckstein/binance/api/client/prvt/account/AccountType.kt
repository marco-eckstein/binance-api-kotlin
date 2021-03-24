@file:UseSerializers(BigDecimalAsPlainStringSerializer::class)

package com.marcoeckstein.binance.api.client.prvt.account

import com.marcoeckstein.binance.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AccountType.Serializer::class)
enum class AccountType(internal val serializedName: String?) {

    SPOT(null),
    CROSS_MARGIN("MARGIN"),
    ISOLATED_MARGIN("ISOLATED_MARGIN"),
    ;

    companion object {

        fun ofSerializedName(serializedName: String?) =
            values().single { it.serializedName == serializedName }
    }

    internal object Serializer : KSerializer<AccountType> {

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor(AccountType::class.simpleName!!, PrimitiveKind.STRING)

        @ExperimentalSerializationApi
        override fun serialize(encoder: Encoder, value: AccountType) {
            if (value.serializedName == null)
                encoder.encodeNull()
            else
                encoder.encodeString(value.serializedName)
        }

        override fun deserialize(decoder: Decoder): AccountType =
            ofSerializedName(decoder.decodeString())
    }
}
