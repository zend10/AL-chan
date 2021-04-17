package com.zen.alchan.data.repository

import com.zen.alchan.data.model.HomeData
import io.reactivex.Observable

interface ContentRepository {
    fun getHomeData(): Observable<HomeData>
}