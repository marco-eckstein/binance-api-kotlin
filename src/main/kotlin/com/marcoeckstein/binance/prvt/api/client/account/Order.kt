@file:UseSerializers(BigDecimalAsPlainStringSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account

import com.binance.api.client.domain.OrderSide
import com.binance.api.client.domain.OrderType
import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class Order(
    @[Contextual SerialName("time")]
    override val timestamp: Instant,
    @Contextual
    val updateTime: Instant?,
    val orderId: Long,
    val side: OrderSide,
    val symbol: String,
    val baseAsset: String,
    val quoteAsset: String,
    val price: BigDecimal,
    val executedPrice: BigDecimal,
    val origQty: BigDecimal,
    val executedQty: BigDecimal,
    val executedQuoteQty: BigDecimal,
    val delegateMoney: BigDecimal,
    val stopPrice: Double,
    val type: OrderType,
    @Serializable(with = OrderStatusSerializer::class) val status: OrderStatus,
    val origStatus: String? = null,
    val accountId: Long?,
    val userId: Long,
    val clientOrderId: String,
    val origClientOrderId: String?,
    val productName: String?,
    val matchingUnitType: String,
    val orderType: String,
    val language: String? = null,
    val hasDetail: Boolean? = null,
    val statusCode: String? = null,
    val orderListId: Long,
    val msgAuth: String,
    // It is unclear which types the following properties have, since they seem to always contain the same values:
    // timeInForce: null
    // public val email: null,
    // public val tradedVolume: 0
) : Timestamped {

    internal object OrderStatusSerializer : KSerializer<OrderStatus> {

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor(OrderStatus::class.simpleName!!, PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: OrderStatus) {
            encoder.encodeString(
                value.name.split('_').joinToString(" ") { it.capitalize() }
            )
        }

        override fun deserialize(decoder: Decoder): OrderStatus =
            OrderStatus.valueOf(
                decoder.decodeString().toUpperCase().replace(' ', '_')
            )
    }
}
