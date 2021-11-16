package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.client.prvt.account.FiatDepositAndWithdrawHistoryEntry
import com.marcoeckstein.binance.api.client.prvt.account.Payment
import com.marcoeckstein.binance.api.client.prvt.account.request.FiatDepositAndWithdrawHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.account.request.PaymentHistoryQuery
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult2
import com.marcoeckstein.binance.api.client.prvt.response.QueryResult3
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

internal interface FiatApiV1Service {

    @POST("private/fiatpayment/transactions/get-order-history")
    fun getFiatDepositAndWithdrawHistory(
        @HeaderMap headers: Map<String, String>,
        @Body query: FiatDepositAndWithdrawHistoryQuery
    ): Call<QueryResult2<FiatDepositAndWithdrawHistoryEntry>>

    @POST("private/ocbs/get-user-payment-history")
    fun getPaymentHistory(
        @HeaderMap headers: Map<String, String>,
        @Body query: PaymentHistoryQuery
    ): Call<QueryResult3<Payment>>

    companion object {

        const val BaseUrl = "https://www.binance.com/bapi/fiat/v1/"
    }
}
