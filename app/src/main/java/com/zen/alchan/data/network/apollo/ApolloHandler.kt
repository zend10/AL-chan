package com.zen.alchan.data.network.apollo

import com.apollographql.apollo3.ApolloClient

interface ApolloHandler {
    val apolloClient: ApolloClient
}