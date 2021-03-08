package com.marcoeckstein.binance.prvt.api.client

import com.marcoeckstein.binance.prvt.api.client.account.CrossMarginBorrowing
import com.marcoeckstein.binance.prvt.api.client.account.CrossMarginRepayment
import com.marcoeckstein.binance.prvt.api.client.account.CrossMarginTransfer
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginBorrowing
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginRepayment
import com.marcoeckstein.binance.prvt.api.client.account.IsolatedMarginTransfer
import com.marcoeckstein.binance.prvt.api.client.account.Order
import com.marcoeckstein.binance.prvt.api.client.account.PaidInterest
import com.marcoeckstein.binance.prvt.api.client.account.Trade
import com.marcoeckstein.binance.prvt.api.client.account.request.OpenOrdersQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.OrderHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.account.request.TradeHistoryQuery
import com.marcoeckstein.binance.prvt.api.client.response.QueryResult1
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Query

internal interface ExchangeApiV1Service {

    @POST("streamer/order/get-open-orders")
    fun getOpenOrders(
        @HeaderMap headers: Map<String, String>,
        @Body query: OpenOrdersQuery
    ): Call<QueryResult1<Order>>

    @POST("streamer/order/get-trade-orders")
    fun getOrderHistory(
        @HeaderMap headers: Map<String, String>,
        @Body query: OrderHistoryQuery
    ): Call<QueryResult1<Order>>

    @POST("streamer/trade/get-user-trades")
    fun getTradeHistory(
        @HeaderMap headers: Map<String, String>,
        @Body query: TradeHistoryQuery
    ): Call<QueryResult1<Trade>>

    @GET("margin/borrow-history")
    fun getCrossMarginBorrowHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<CrossMarginBorrowing>>

    @GET("isolated-margin/borrow-history")
    fun getIsolatedMarginBorrowingHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<IsolatedMarginBorrowing>>

    @GET("margin/repay-history")
    fun getCrossMarginRepayHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<CrossMarginRepayment>>

    @GET("isolated-margin/repay-history")
    fun getIsolatedMarginRepaymentHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<IsolatedMarginRepayment>>

    @GET("margin/transfer-history")
    fun getCrossMarginTransferHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<CrossMarginTransfer>>

    @GET("isolated-margin/transfer-history")
    fun getIsolatedMarginTransferHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<IsolatedMarginTransfer>>

    @GET("margin/interest-history")
    fun getCrossMarginInterestHistory(
        @HeaderMap headers: Map<String, String>,
        @Query("current") current: Int?,
        @Query("size") size: Int?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
    ): Call<QueryResult1<PaidInterest>>

    companion object {

        const val BaseUrl = "https://www.binance.com/exchange-api/v1/private/"
    }
}
