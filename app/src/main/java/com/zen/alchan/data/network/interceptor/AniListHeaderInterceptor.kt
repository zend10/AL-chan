package com.zen.alchan.data.network.interceptor

import com.zen.alchan.data.manager.UserManager
import okhttp3.Interceptor
import okhttp3.Response

class AniListHeaderInterceptor(private val userManager: UserManager) : HeaderInterceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            if (userManager.isAuthenticated) {
                request().newBuilder()
                    .addHeader("Authorization", "Bearer ${userManager.bearerToken}")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
            } else {
                request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
            }
        )
    }
}