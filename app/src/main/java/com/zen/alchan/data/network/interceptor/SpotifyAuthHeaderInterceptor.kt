package com.zen.alchan.data.network.interceptor

import com.zen.alchan.data.manager.BrowseManager
import okhttp3.Interceptor
import okhttp3.Response

class SpotifyAuthHeaderInterceptor(private val browseManager: BrowseManager) : HeaderInterceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request().newBuilder()
                .addHeader("Authorization", browseManager.spotifyApiKey)
                .build()
        )
    }
}