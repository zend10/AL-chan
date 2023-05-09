package com.zen.alchan.data.network.apollo

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.zen.alchan.data.network.apollo.adapter.CountryCodeAdapter
import com.zen.alchan.data.network.apollo.adapter.JsonAdapter
import com.zen.alchan.data.network.interceptor.HeaderInterceptor
import com.zen.alchan.type.CountryCode
import com.zen.alchan.type.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class AniListApolloHandler(
    private val headerInterceptor: HeaderInterceptor,
    private val baseUrl: String
) : ApolloHandler {

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
        redactHeader("Authorization")
        redactHeader("Cookie")
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addNetworkInterceptor(headerInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    override val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl(baseUrl)
        .okHttpClient(okHttpClient)
        .addCustomScalarAdapter(Json.type, JsonAdapter())
        .addCustomScalarAdapter(CountryCode.type, CountryCodeAdapter())
        .build()
}