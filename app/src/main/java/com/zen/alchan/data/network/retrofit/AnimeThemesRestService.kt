package com.zen.alchan.data.network.retrofit

import com.zen.alchan.data.response.animethemes.AnimePaginationResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface AnimeThemesRestService {

    @GET("anime")
    fun getAnimeDetails(
        @Query("filter[external_id]") malId: Int,
        @QueryMap queries: Map<String, String> = mapOf(
            "filter[has]" to "resources",
            "filter[site]" to "MyAnimeList",
            "include" to "animethemes.song.artists,animethemes.animethemeentries.videos.audio"
        )
    ): Observable<AnimePaginationResponse>
}