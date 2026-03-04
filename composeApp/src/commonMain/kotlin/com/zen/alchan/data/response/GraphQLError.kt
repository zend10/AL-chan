package com.zen.alchan.data.response

import kotlinx.serialization.Serializable

@Serializable
data class GraphQLError(
    val errors: List<String>
)
