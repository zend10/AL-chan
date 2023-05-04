package com.zen.alchan.data.network.apollo

import com.apollographql.apollo.ApolloClient
import com.zen.alchan.data.network.OkHttpHandler
import com.zen.alchan.data.network.apollo.adapter.CountryCodeAdapter
import com.zen.alchan.data.network.apollo.adapter.JsonAdapter
import com.zen.alchan.data.network.interceptor.HeaderInterceptor
import type.CustomType

class AniListApolloHandler(
    private val okHttpHandler: OkHttpHandler,
    private val headerInterceptor: HeaderInterceptor,
    private val baseUrl: String
) : ApolloHandler {

    private val okHttpClient = okHttpHandler.okHttpClientBuilder
        .addNetworkInterceptor(headerInterceptor)
        .build()

    override val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl(baseUrl)
        .okHttpClient(okHttpClient)
        .addCustomTypeAdapter(CustomType.JSON, JsonAdapter())
        .addCustomTypeAdapter(CustomType.COUNTRYCODE, CountryCodeAdapter())
        .build()
}