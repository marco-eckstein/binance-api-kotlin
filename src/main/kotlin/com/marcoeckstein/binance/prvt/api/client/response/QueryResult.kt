package com.marcoeckstein.binance.prvt.api.client.response

internal interface QueryResult<T : Any> {

    val code: String
    val message: String?
    val messageDetail: String?
    val items: List<T>
    val success: Boolean
}
