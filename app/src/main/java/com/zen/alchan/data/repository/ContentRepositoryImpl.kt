package com.zen.alchan.data.repository

import com.zen.alchan.data.datasource.ContentDataSource
import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.model.HomeData
import io.reactivex.Observable

class ContentRepositoryImpl(private val contentDataSource: ContentDataSource) : ContentRepository {

    override fun getHomeData(): Observable<HomeData> {
        return contentDataSource.getHomeQuery().map { it.data?.convert() ?: HomeData() }
    }
}