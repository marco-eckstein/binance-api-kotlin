package com.marcoeckstein.binance.api.client.public

import com.marcoeckstein.binance.api.client.BinanceApiException
import retrofit2.Call
import java.time.Instant

class BinancePublicApiRestClient internal constructor(
    private val sapiV1Service: SapiV1Service,
) {

    fun getAllCoinsInformation(recvWindow: Instant? = null): List<CoinInfo> =
        execute(
            sapiV1Service.getAllCoinsInformation(recvWindow?.toEpochMilli())
        )

    private fun <T : Any> execute(call: Call<out T>): T {
        val response = call.execute()
        if (!response.isSuccessful)
            throw BinanceApiException(response.code().toString() + " " + response.errorBody()?.string())
        return response.body()!!
    }
}
