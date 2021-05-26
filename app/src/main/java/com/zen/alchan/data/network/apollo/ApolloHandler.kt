package com.zen.alchan.data.network.apollo

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

interface ApolloHandler {
    val apolloClient: ApolloClient
    val apiVersion: Int
}