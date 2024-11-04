package com.productfinder.data.network

import com.productfinder.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Singleton
class AppOkHttpClient : OkHttpClient() {
    fun getOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder: Builder = Builder().protocols(listOf(Protocol.HTTP_1_1))
            .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(READ_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) okHttpClientBuilder.addInterceptor(
            setLogging()
        )
        return okHttpClientBuilder.build()
    }


    private fun setLogging(): Interceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    companion object {
        private const val CONNECT_TIMEOUT = 60 // 60 seconds
        private const val READ_WRITE_TIMEOUT = 120 // 120 seconds
    }
}