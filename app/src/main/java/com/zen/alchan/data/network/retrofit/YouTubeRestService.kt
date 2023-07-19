package com.zen.alchan.data.network.retrofit

import com.zen.alchan.data.response.youtube.VideoSearchResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface YouTubeRestService {

    @GET("search")
    fun getYouTubeVideo(
        @Query("key") key: String,
        @Query("q") query: String,
        @QueryMap queries: Map<String, String> = mapOf(
            "part" to "snippet",
            "type" to "video",
            "maxResults" to "1"
        )
    ): Observable<VideoSearchResponse>
}