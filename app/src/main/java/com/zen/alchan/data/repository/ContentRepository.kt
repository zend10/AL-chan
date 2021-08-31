package com.zen.alchan.data.repository

import com.zen.alchan.data.response.HomeData
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.data.response.Genre
import io.reactivex.Observable

interface ContentRepository {
    fun getHomeData(source: Source? = null): Observable<HomeData>
    fun getGenres(): Observable<List<Genre>>
    fun getTags(): Observable<List<MediaTag>>
}