package com.zen.alchan.data.network

import com.zen.alchan.data.request.GraphQLRequest
import com.zen.alchan.data.response.GraphQLResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonElement


class KtorHttpClient(val httpClient: HttpClient, val baseUrl: String) {

    fun setBearerToken(token: String) {
        httpClient.config {
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(token, null)
                    }
                }
            }
        }
    }

    suspend inline fun <reified T> query(
        query: String,
        variables: Map<String, JsonElement> = mapOf()
    ): T {
        val request = GraphQLRequest(query, variables)
        val response = httpClient.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<GraphQLResponse<T>>().data
    }
}