@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.binance.api.client.domain.OrderSide
import com.binance.api.client.domain.OrderStatus
import com.binance.api.client.domain.OrderType
import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.KSerializer
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
    val orderId: Long,
    val userId: Long,
    val accountId: Long?,
    val symbol: String,
    val clientOrderId: String,
    val origClientOrderId: String?,
    val price: BigDecimal,
    val origQty: BigDecimal,
    val executedQty: BigDecimal,
    val executedQuoteQty: BigDecimal,
    @Serializable(with = OrderStatusSerializer::class) val status: OrderStatus,
    val origStatus: String? = null,
    val type: OrderType,
    val side: OrderSide,
    val stopPrice: Double,
    val time: Instant,
    val updateTime: Instant?,
    val baseAsset: String,
    val quoteAsset: String,
    val delegateMoney: BigDecimal,
    val executedPrice: BigDecimal,
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
) {

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
