package com.zen.alchan.data.repository

import com.zen.alchan.data.response.HomeData
import com.zen.alchan.helper.enums.Source
import io.reactivex.Observable

interface ContentRepository {
    fun getHomeData(source: Source? = null): Observable<HomeData>
}