package com.zen.alchan.data.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, JsonElement> = emptyMap()
)