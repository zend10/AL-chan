package com.zen.alchan.data.network

import okhttp3.OkHttpClient

interface OkHttpHandler {
    val okHttpClientBuilder: OkHttpClient.Builder
}