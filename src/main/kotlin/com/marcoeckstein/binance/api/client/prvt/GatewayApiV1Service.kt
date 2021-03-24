package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.client.prvt.account.Distribution
import com.marcoeckstein.binance.api.client.prvt.account.FiatDepositAndWithdrawHistoryEntry
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountDetail
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginAccountPosition
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginInterest
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginRebate
import com.marcoeckstein.binance.api.client.prvt.account.Payment
import com.marcoeckstein.binance.api.client.prvt.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.api.client.prvt.account.earn.FlexibleSavingsPosition
import com.marcoeckstein.binance.api.client.prvt.account.earn.LendingType
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingPosition
import com.marcoeckstein.binance.api.client.prvt.account.request.DistributionHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.FiatDepositAndWithdrawHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.PaymentHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult1
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult2
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult3
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
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

    @POST("friendly/fiatpayment/transactions/get-order-history")
    fun getFiatDepositAndWithdrawHistory(
        @HeaderMap headers: Map<String, String>,
        @Body query: FiatDepositAndWithdrawHistoryQuery
    ): Call<QueryResult2<FiatDepositAndWithdrawHistoryEntry>>

    @POST("private/asset/asset/user-asset-dividend")
    fun getDistributionHistory(
        @HeaderMap headers: Map<String, String>,
        @Body query: DistributionHistoryQuery
    ): Call<QueryResult1<Distribution>>

    @POST("private/ocbs/get-user-payment-history")
    fun getPaymentHistory(
        @HeaderMap headers: Map<String, String>,
        @Body query: PaymentHistoryQuery
    ): Call<QueryResult3<Payment>>

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

    @GET("private/pos/project-interest/history")
    fun getLockedStakingInterestHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("pageIndex") pageIndex: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("asset") asset: String?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<LockedStakingInterest>>

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

    @GET("private/isolated-margin/interest-history")
    fun getIsolatedMarginInterestHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<IsolatedMarginInterest>>

    companion object {

        const val BaseUrl = "https://www.binance.com/gateway-api/v1/"
    }
}
