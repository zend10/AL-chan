package com.zen.alchan.data.network.apollo

import com.apollographql.apollo.ApolloClient
import com.zen.alchan.data.network.header.HeaderInterceptor
import com.zen.alchan.helper.Constant

class AniListApolloHandler(private val headerInterceptor: HeaderInterceptor) : BaseApolloHandler(headerInterceptor) {

    override val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl(Constant.ANILIST_API_URL)
        .okHttpClient(okHttpClient)
        .build()
}