package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginInterest
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginRebate
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult1
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

internal interface MarginApiV1Service {

    @GET("private/isolated-margin/interest-history")
    fun getIsolatedMarginInterestHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
        @Query("archived") archived: Boolean?,
    ): Call<QueryResult1<IsolatedMarginInterest>>

    @GET("private/isolated-margin/order/bnb-discount-histories")
    fun getIsolatedMarginRebateHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("asset") asset: String?,
        @Query("symbol") symbol: String?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<IsolatedMarginRebate>>

    companion object {

        const val BaseUrl = "https://www.binance.com/bapi/margin/v1/"
    }
}
