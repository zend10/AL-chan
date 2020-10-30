package com.zen.alchan.data.network

import com.zen.alchan.data.response.YouTubeSearch
import com.zen.alchan.helper.Constant
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface YouTubeRestService {

    @GET("search")
    fun searchVideo(
        @Query("key") key: String,
        @Query("part") part: String,
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("maxResults") maxResults: Int
    ): Call<YouTubeSearch>

    companion object {
        operator fun invoke(): YouTubeRestService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constant.YOUTUBE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(YouTubeRestService::class.java)
        }
    }
}