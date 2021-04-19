package com.zen.alchan.data.repository

import com.zen.alchan.data.response.HomeData
import io.reactivex.Observable

interface ContentRepository {
    fun getHomeData(): Observable<HomeData>
}