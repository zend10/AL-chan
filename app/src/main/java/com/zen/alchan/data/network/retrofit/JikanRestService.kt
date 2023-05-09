package com.zen.alchan.data.network.retrofit

import com.zen.alchan.data.response.mal.MangaResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface JikanRestService {

    @GET("manga/{mangaMalId}/full")
    fun getMangaDetails(@Path("mangaMalId") mangaMalId: Int): Observable<MangaResponse>
}