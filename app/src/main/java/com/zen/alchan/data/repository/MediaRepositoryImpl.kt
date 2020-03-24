package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.MediaDataSource
import com.zen.alchan.data.localstorage.MediaManager
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MediaRepositoryImpl(private val mediaDataSource: MediaDataSource,
                          private val mediaManager: MediaManager
) : MediaRepository {

    override val genreList: List<String>
        get() = mediaManager.genreList

    override val genreListLastRetrieved: Long?
        get() = mediaManager.genreListLastRetrieved

    @SuppressLint("CheckResult")
    override fun getGenre() {
        mediaDataSource.getGenre().subscribeWith(object : Observer<Response<GenreQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }
            override fun onNext(t: Response<GenreQuery.Data>) {
                if (!t.hasErrors() && !t.data()?.GenreCollection().isNullOrEmpty()) {
                    mediaManager.setGenreList(t.data()?.GenreCollection()!!)
                }
            }
            override fun onError(e: Throwable) { e.printStackTrace() }
            override fun onComplete() { }
        })
    }
}