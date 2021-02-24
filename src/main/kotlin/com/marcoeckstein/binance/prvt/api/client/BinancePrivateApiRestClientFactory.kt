package com.marcoeckstein.binance.prvt.api.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@ExperimentalSerializationApi
class BinancePrivateApiRestClientFactory private constructor(
    curlAddressPosix: String,
    sendRedundantHeaders: Boolean
) {

    private val headers: Map<String, String> =
        parseHeadersFromCurlAddressPosix(curlAddressPosix).filter {
            sendRedundantHeaders || it.key in setOf(
                "bnc-uuid",
                "clienttype",
                "csrftoken",
                "Cookie",
                "User-Agent"
            )
        }

    /**
     * @param sleepMillis The number of ms to sleep after a request, in order to prevent an excessive request rate.
     */
    fun newRestClient(
        logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC,
        sleepMillis: Long = 1000
    ): BinancePrivateApiRestClient {
        val okHttpClient =
            OkHttpClient.Builder().apply {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = logLevel }
                )
                addInterceptor { chain ->
                    Thread.sleep(sleepMillis)
                    chain.proceed(chain.request())
                }
            }.build()
        return newRestClient(okHttpClient)
    }

    fun newRestClient(okHttpClient: OkHttpClient): BinancePrivateApiRestClient =
        BinancePrivateApiRestClient(
            headers,
            newRetrofit(okHttpClient, ExchangeApiV1Service.BaseUrl).create(ExchangeApiV1Service::class.java),
            newRetrofit(okHttpClient, GatewayApiV1Service.BaseUrl).create(GatewayApiV1Service::class.java),
            newRetrofit(okHttpClient, GatewayApiV3Service.BaseUrl).create(GatewayApiV3Service::class.java)
        )

    private fun newRetrofit(client: OkHttpClient, baseUrl: String): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(
                Json { ignoreUnknownKeys = true }.asConverterFactory(MediaType.get("application/json"))
            )
            .build()

    companion object {

        /**
         * @param curlAddressPosix A cURL (POSIX) call as issued by the Binance website.
         *                         It must include the confidential headers that allow authentication.
         *                         See the project's README file for instructions on how to obtain it.
         *
         * @param sendAllHeaders So far, some headers are not required for the private API to function.
         *                       But it may happen that more headers than anticipated become required.
         *                       If this happens, pass [true] for this parameter.
         */
        fun newInstance(
            curlAddressPosix: String,
            sendAllHeaders: Boolean = false
        ) = BinancePrivateApiRestClientFactory(curlAddressPosix, sendAllHeaders)

        internal fun parseHeadersFromCurlAddressPosix(s: String): Map<String, String> =
            Regex("""-H\s'([^:]+):\s([^']+)'""").findAll(s)
                .map { it.groupValues[1] to it.groupValues[2] }
                .toMap()
    }
}
