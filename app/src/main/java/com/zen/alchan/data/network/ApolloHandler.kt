package com.zen.alchan.data.network

import com.apollographql.apollo.ApolloClient
import com.zen.alchan.helper.Constant
import okhttp3.OkHttpClient
import org.koin.android.ext.android.get
import java.util.concurrent.TimeUnit

class ApolloHandler(private val headerInterceptor: HeaderInterceptor) {

    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(headerInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    val apolloClient = ApolloClient.builder()
        .serverUrl(Constant.ANILIST_API_URL)
        .okHttpClient(okHttpClient)
        .build()
}