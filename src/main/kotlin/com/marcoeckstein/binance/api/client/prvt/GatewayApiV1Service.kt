package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountDetail
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountPosition
import com.marcoeckstein.binance.api.client.prvt.account.earn.FlexibleSavingsPosition
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingPosition
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult1
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

@Suppress("LongParameterList") // detekt
internal interface GatewayApiV1Service {

    @GET("private/isolated-margin/isolated-margin-account-details?requireCoupon=true")
    fun getIsolatedMarginAccountDetails(
        @HeaderMap headers: Map<String, String>
    ): Call<QueryResult1<IsolatedMarginAccountDetail>>

    @GET("private/isolated-margin/isolated-margin-asset-position")
    fun getIsolatedMarginAccountPositions(
        @HeaderMap headers: Map<String, String>
    ): Call<QueryResult1<IsolatedMarginAccountPosition>>

    @GET("private/lending/daily/token/position")
    fun getFlexibleSavingsPositions(
        @HeaderMap headers: Map<String, String>,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
    ): Call<QueryResult1<FlexibleSavingsPosition>>

    @GET("private/pos/project-position/list")
    fun getLockedStakingPositions(
        @HeaderMap headers: Map<String, String>,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
    ): Call<QueryResult1<LockedStakingPosition>>

    companion object {

        const val BaseUrl = "https://www.binance.com/gateway-api/v1/"
    }
}
