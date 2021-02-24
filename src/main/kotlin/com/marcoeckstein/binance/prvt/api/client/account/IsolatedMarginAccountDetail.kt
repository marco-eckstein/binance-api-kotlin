@file:UseSerializers(
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

@Serializable
data class IsolatedMarginAccountDetail(
    val base: Asset,
    val quote: Asset,
    // More properties omitted
) {

    @Serializable
    data class Asset(
        val assetName: String,
        /**
         * Aka available balance
         */
        val free: BigDecimal,
        val locked: BigDecimal,
        val borrowed: BigDecimal,
        val interest: BigDecimal,
        val netAsset: BigDecimal,
        /**
         * Aka total balance
         */
        val total: BigDecimal,
        // More properties omitted
    ) {

        init {
            require(free >= BigDecimal.ZERO)
            require(locked >= BigDecimal.ZERO)
            require(borrowed >= BigDecimal.ZERO)
            require(interest >= BigDecimal.ZERO)
            require(total >= BigDecimal.ZERO)
            require(total == free + locked)
            require(netAsset == total - (borrowed + interest))
        }
    }
}
