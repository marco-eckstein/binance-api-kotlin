package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.client.prvt.account.AssetBalance
import com.marcoeckstein.binance.api.client.prvt.account.request.AssetBalanceQuery
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult1
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

internal interface GatewayApiV3Service {

    @POST("asset-service/asset/get-user-asset")
    fun getSpotAccountBalances(
        @HeaderMap headers: Map<String, String>,
        @Body query: AssetBalanceQuery
    ): Call<QueryResult1<AssetBalance>>

    companion object {

        const val BaseUrl = "https://www.binance.com/gateway-api/v3/private/"
    }
}
