package com.zen.alchan.data.network.service

import com.zen.alchan.data.network.header.SpotifyAuthHeaderInterceptor
import com.zen.alchan.data.network.header.SpotifyAuthHeaderInterceptorImpl
import com.zen.alchan.data.network.header.SpotifyHeaderInterceptor
import com.zen.alchan.data.response.SpotifySearch
import com.zen.alchan.helper.Constant
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface SpotifyRestService {

    @GET("search")
    fun searchTrack(
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("limit") limit: Int
    ): Call<SpotifySearch>

    companion object {
        operator fun invoke(spotifyHeaderInterceptor: SpotifyHeaderInterceptor): SpotifyRestService {
            val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(spotifyHeaderInterceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constant.SPOTIFY_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SpotifyRestService::class.java)
        }
    }
}