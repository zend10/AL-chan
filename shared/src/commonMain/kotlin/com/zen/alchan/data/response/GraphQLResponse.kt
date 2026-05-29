package com.zen.alchan.data.response

import kotlinx.serialization.Serializable

@Serializable
data class GraphQLResponse<T>(
    val data: T,
    val errors: List<GraphQLError>? = null
)
