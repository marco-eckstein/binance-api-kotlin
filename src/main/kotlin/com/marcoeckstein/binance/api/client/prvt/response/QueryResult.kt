package com.marcoeckstein.binance.api.client.prvt.response

internal interface QueryResult<T : Any> {

    val code: String
    val message: String?
    val messageDetail: String?
    val items: List<T>
    val success: Boolean
}
