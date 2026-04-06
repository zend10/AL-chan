package com.zen.alchan.data.network

import com.zen.alchan.data.request.GraphQLRequest
import com.zen.alchan.data.response.GraphQLResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


class KtorHttpClient(val httpClient: HttpClient, val baseUrl: String) {

    suspend inline fun <reified T> query(
        query: String,
        variables: Map<String, JsonElement>
    ): T {
        val request = GraphQLRequest(query, variables)
        val response = httpClient.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<GraphQLResponse<T>>().data
    }
}