package com.zen.alchan.data.network.apollo

import com.apollographql.apollo.ApolloClient
import com.zen.alchan.data.network.header.HeaderInterceptor

class AniListApolloHandler(private val headerInterceptor: HeaderInterceptor, baseUrl: String) : BaseApolloHandler(headerInterceptor) {

    override val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl(baseUrl)
        .okHttpClient(okHttpClient)
        .build()
}