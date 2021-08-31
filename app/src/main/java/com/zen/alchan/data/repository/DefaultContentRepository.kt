package com.zen.alchan.data.repository

import com.zen.alchan.data.datasource.ContentDataSource
import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.manager.ContentManager
import com.zen.alchan.data.response.HomeData
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.moreThanADay
import com.zen.alchan.data.response.Genre
import com.zen.alchan.helper.pojo.SaveItem
import com.zen.alchan.helper.utils.NotInStorageException
import io.reactivex.Observable

class DefaultContentRepository(
    private val contentDataSource: ContentDataSource,
    private val contentManager: ContentManager
) : BaseRepository(), ContentRepository {

    override fun getHomeData(source: Source?): Observable<HomeData> {
        return when(source) {
            Source.NETWORK -> getHomeDataFromNetwork()
            Source.CACHE -> getHomeDataFromCache()
            else -> {
                val savedItem = contentManager.homeData
                if (savedItem == null || savedItem.saveTime.moreThanADay()) {
                    getHomeDataFromNetwork()
                } else {
                    Observable.just(savedItem.data)
                }
            }
        }
    }

    private fun getHomeDataFromNetwork(): Observable<HomeData> {
        return contentDataSource.getHomeQuery().map {
            val newHomeData = it.data?.convert() ?: HomeData()
            contentManager.homeData = SaveItem(newHomeData)
            newHomeData
        }
    }

    private fun getHomeDataFromCache(): Observable<HomeData> {
        val savedItem = contentManager.homeData?.data
        return if (savedItem != null) Observable.just(savedItem) else Observable.error(NotInStorageException())
    }

    override fun getGenres(): Observable<List<Genre>> {
        val savedItem = contentManager.genres
        return if (savedItem == null || savedItem.saveTime.moreThanADay()) {
            contentDataSource.getGenres()
                .map {
                    val newGenres = it.data?.convert() ?: listOf()
                    contentManager.genres = SaveItem(newGenres)
                    newGenres
                }
                .doOnError {
                    passSavedDataOrThrowable(savedItem?.data, it)
                }

        } else {
            Observable.just(savedItem.data)
        }
    }

    override fun getTags(): Observable<List<MediaTag>> {
        val savedItem = contentManager.tags
        return if (savedItem == null || savedItem.saveTime.moreThanADay()) {
            contentDataSource.getTags()
                .map {
                    val newTags = it.data?.convert() ?: listOf()
                    contentManager.tags = SaveItem(newTags)
                    newTags
                }
                .doOnError {
                    passSavedDataOrThrowable(savedItem?.data, it)
                }
        } else {
            Observable.just(savedItem.data)
        }
    }
}