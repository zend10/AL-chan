package com.zen.alchan.data.network.retrofit

import com.zen.alchan.data.response.spotify.TrackSearchResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SpotifyRestService {

    @GET("search")
    fun getSpotifyTrack(
        @Query("q") query: String,
        @QueryMap queries: Map<String, String> = mapOf(
            "type" to "track",
            "limit" to "1"
        )
    ): Observable<TrackSearchResponse>
}