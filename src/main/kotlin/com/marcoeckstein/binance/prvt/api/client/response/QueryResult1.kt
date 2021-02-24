package com.marcoeckstein.binance.prvt.api.client.response

import kotlinx.serialization.Serializable

@Serializable
internal class QueryResult1<T : Any>(
    override val code: String,
    override val message: String?,
    override val messageDetail: String?,
    override val success: Boolean,
    val total: Int? = null,
    val data: List<T>?,
) : QueryResult<T> {

    override val items: List<T> get() = data ?: listOf()
}
