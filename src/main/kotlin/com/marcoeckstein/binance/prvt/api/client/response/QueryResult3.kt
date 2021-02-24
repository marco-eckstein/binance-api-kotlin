package com.marcoeckstein.binance.prvt.api.client.response

import kotlinx.serialization.Serializable

@Serializable
internal class QueryResult3<T : Any>(
    override val code: String,
    override val message: String?,
    override val messageDetail: String?,
    override val success: Boolean,
    val data: DataContainer1<T>?,
) : QueryResult<T> {

    override val items: List<T> get() = data?.rows?.dataList ?: listOf()

    @Serializable
    class DataContainer1<T>(
        val rows: DataContainer2<T>?,
    )

    @Serializable
    class DataContainer2<T>(
        val count: Int,
        val dataList: List<T>?,
    )
}
