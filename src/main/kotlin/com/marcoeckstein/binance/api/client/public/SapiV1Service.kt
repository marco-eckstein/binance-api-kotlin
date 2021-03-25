package com.marcoeckstein.binance.api.client.public

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@Suppress("LongParameterList") // detekt
internal interface SapiV1Service {

    @GET("capital/config/getall")
    fun getAllCoinsInformation(
        @Query("recvWindow") recvWindow: Long? = null,
    ): Call<List<CoinInfo>>

    companion object {

        const val BaseUrl = " https://api.binance.com/sapi/v1/"
    }
}
