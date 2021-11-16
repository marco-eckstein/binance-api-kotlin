package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.client.prvt.account.Distribution
import com.marcoeckstein.binance.api.client.prvt.account.request.DistributionHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult1
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

internal interface AssetApiV1Service {

    @POST("private/asset/asset/user-asset-dividend")
    fun getDistributionHistory(
        @HeaderMap headers: Map<String, String>,
        @Body query: DistributionHistoryQuery
    ): Call<QueryResult1<Distribution>>

    companion object {

        const val BaseUrl = "https://www.binance.com/bapi/asset/v1/"
    }
}
