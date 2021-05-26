package com.zen.alchan.data.network.apollo

import com.apollographql.apollo.ApolloClient
import com.zen.alchan.data.network.apollo.adapter.CountryCodeAdapter
import com.zen.alchan.data.network.apollo.adapter.JsonAdapter
import com.zen.alchan.data.network.header.HeaderInterceptor
import type.CustomType

class AniListApolloHandler(
    private val headerInterceptor: HeaderInterceptor,
    private val baseUrl: String,
    private val version: Int
) : BaseApolloHandler(headerInterceptor) {

    override val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl(baseUrl)
        .okHttpClient(okHttpClient)
        .addCustomTypeAdapter(CustomType.JSON, JsonAdapter())
        .addCustomTypeAdapter(CustomType.COUNTRYCODE, CountryCodeAdapter())
        .build()

    override val apiVersion: Int = version
}