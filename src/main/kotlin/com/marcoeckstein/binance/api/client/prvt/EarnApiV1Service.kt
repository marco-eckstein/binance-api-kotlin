package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.client.prvt.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.api.client.prvt.account.earn.LendingType
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult1
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

@Suppress("LongParameterList") // detekt
internal interface EarnApiV1Service {

    @GET("private/pos/union/getUserInterestRecord")
    fun getLockedStakingInterestHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("pageIndex") pageIndex: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("asset") asset: String?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
        @Query("lendingType") lendingType: LendingType?,
    ): Call<QueryResult1<LockedStakingInterest>>

    @GET("private/lending/union/interestHistory/list")
    fun getFlexibleSavingsInterestHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("pageIndex") pageIndex: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("asset") asset: String?,
        @Query("lendingType") lendingType: LendingType?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<FlexibleSavingsInterest>>

    companion object {

        const val BaseUrl = "https://www.binance.com/bapi/earn/v1/"
    }
}
