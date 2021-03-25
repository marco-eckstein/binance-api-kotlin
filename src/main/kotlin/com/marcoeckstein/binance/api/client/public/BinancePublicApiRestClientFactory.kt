package com.marcoeckstein.binance.api.client.public

import com.binance.api.client.constant.BinanceApiConstants
import com.binance.api.client.security.HmacSHA256Signer
import com.marcoeckstein.binance.api.client.ServiceFactory
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.Instant

@ExperimentalSerializationApi
class BinancePublicApiRestClientFactory private constructor(
    private val apiKey: String,
    private val secret: String,
) {

    /**
     * @param sleepMillis The number of ms to sleep after a request, in order to prevent an excessive request rate.
     */
    fun newRestClient(
        logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY,
        sleepMillis: Long = 1000
    ): BinancePublicApiRestClient {
        val okHttpClient =
            OkHttpClient.Builder().apply {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = logLevel }
                )
                addInterceptor { chain ->
                    Thread.sleep(sleepMillis)
                    chain.proceed(chain.request())
                }
                addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val newRequestBuilder = originalRequest.newBuilder()
                    val urlWithTimestamp =
                        originalRequest.url()
                            .newBuilder()
                            .addQueryParameter("timestamp", Instant.now().toEpochMilli().toString())
                            .build()
                    newRequestBuilder.url(urlWithTimestamp)
                    val newRequest = newRequestBuilder.build()
                    chain.proceed(newRequest)
                }
                addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val newRequestBuilder = originalRequest.newBuilder()
                    val isApiKeyRequired = true
                    val isSignatureRequired = true
                    if (isApiKeyRequired || isSignatureRequired) {
                        newRequestBuilder.addHeader(BinanceApiConstants.API_KEY_HEADER, apiKey)
                    }
                    if (isSignatureRequired) {
                        val payload = originalRequest.url().query()
                        if (!payload.isNullOrEmpty()) {
                            val signature = HmacSHA256Signer.sign(payload, secret)
                            val signedUrl =
                                originalRequest.url()
                                    .newBuilder()
                                    .addQueryParameter("signature", signature)
                                    .build()
                            newRequestBuilder.url(signedUrl)
                        }
                    }
                    val newRequest = newRequestBuilder.build()
                    chain.proceed(newRequest)
                }
            }.build()
        return newRestClient(okHttpClient)
    }

    fun newRestClient(okHttpClient: OkHttpClient): BinancePublicApiRestClient =
        BinancePublicApiRestClient(
            ServiceFactory(okHttpClient).newService(SapiV1Service.BaseUrl, SapiV1Service::class)
        )

    companion object {

        fun newInstance(
            apiKey: String,
            secret: String,
        ) = BinancePublicApiRestClientFactory(apiKey, secret)
    }
}
