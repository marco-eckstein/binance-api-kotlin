package com.marcoeckstein.binance.api.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.marcoeckstein.binance.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlin.reflect.KClass

class ServiceFactory(
    private val client: OkHttpClient
) {

    fun <T : Any> newService(baseUrl: String, clazz: KClass<T>): T =
        Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                    serializersModule = SerializersModule {
                        contextual(InstantAsEpochMilliSerializer)
                    }
                }.asConverterFactory(MediaType.get("application/json"))
            )
            .build()
            .create(clazz.java)
}
