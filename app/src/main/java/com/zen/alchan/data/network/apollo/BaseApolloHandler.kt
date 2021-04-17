package com.zen.alchan.data.network.apollo

import com.zen.alchan.data.network.header.HeaderInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

abstract class BaseApolloHandler(private val headerInterceptor: HeaderInterceptor) : ApolloHandler {

    protected val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(headerInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
}
