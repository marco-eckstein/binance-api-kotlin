package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.client.ServiceFactory
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@ExperimentalSerializationApi
class BinancePrivateApiRestClientFactory private constructor(
    curlAddressPosix: String,
    sendRedundantHeaders: Boolean
) {

    private val headers: Map<String, String> =
        parseHeadersFromCurlAddressPosix(curlAddressPosix).filter {
            sendRedundantHeaders || it.key.toLowerCase() in setOf(
                "bnc-uuid",
                "clienttype",
                "csrftoken",
                "cookie",
                "user-agent"
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

    fun newRestClient(okHttpClient: OkHttpClient): BinancePrivateApiRestClient {
        val serviceFactory = ServiceFactory(okHttpClient)
        return BinancePrivateApiRestClient(
            headers,
            serviceFactory.newService(ExchangeApiV1Service.BaseUrl, ExchangeApiV1Service::class),
            serviceFactory.newService(GatewayApiV1Service.BaseUrl, GatewayApiV1Service::class),
            serviceFactory.newService(GatewayApiV3Service.BaseUrl, GatewayApiV3Service::class),
            serviceFactory.newService(FiatApiV1Service.BaseUrl, FiatApiV1Service::class),
            serviceFactory.newService(AssetApiV1Service.BaseUrl, AssetApiV1Service::class),
            serviceFactory.newService(EarnApiV1Service.BaseUrl, EarnApiV1Service::class),
            serviceFactory.newService(MarginApiV1Service.BaseUrl, MarginApiV1Service::class),
        )
    }

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
