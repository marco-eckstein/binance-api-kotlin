package com.marcoeckstein.binance.api.extra.export

import com.google.common.base.CaseFormat
import com.google.common.collect.Range
import com.marcoeckstein.binance.api.extra.BinanceRestApiFacade
import com.marcoeckstein.binance.api.lib.guava.convert
import kotlinx.serialization.KSerializer
import java.time.Instant
import kotlin.reflect.KClass

internal data class ItemInfo<T : Any>(
    val clazz: KClass<T>,
    val serializer: KSerializer<T>,
    val filename: String,
    val discriminator: Any? = null,
    val getItems: BinanceRestApiFacade.(timeRange: Range<Instant>) -> List<T>,
) {

    companion object {

        inline fun <reified T : Any> of(
            serializer: KSerializer<T>,
            noinline getItems: BinanceRestApiFacade.(timeRange: Range<Instant>) -> List<T>
        ) = of(
            serializer,
            T::class.simpleName!!.convert(CaseFormat.UPPER_CAMEL to CaseFormat.LOWER_HYPHEN) + "-history",
            null,
            getItems
        )

        inline fun <reified T : Any> of(
            serializer: KSerializer<T>,
            filenameWithoutExtension: String,
            discriminator: Any?,
            noinline getItems: BinanceRestApiFacade.(timeRange: Range<Instant>) -> List<T>
        ) = ItemInfo(
            T::class,
            serializer,
            "$filenameWithoutExtension.csv",
            discriminator,
            getItems
        )
    }
}
