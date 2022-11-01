package com.zen.alchan.data.network.apollo

import com.zen.alchan.data.network.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

abstract class BaseApolloHandler(private val headerInterceptor: HeaderInterceptor) : ApolloHandler {

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
        redactHeader("Authorization")
        redactHeader("Cookie")
    }

    protected val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(headerInterceptor)
//        .addInterceptor(httpLoggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
}
