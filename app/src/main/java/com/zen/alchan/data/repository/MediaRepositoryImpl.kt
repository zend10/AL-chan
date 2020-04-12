package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.MediaDataSource
import com.zen.alchan.data.localstorage.MediaManager
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MediaRepositoryImpl(private val mediaDataSource: MediaDataSource,
                          private val mediaManager: MediaManager,
                          private val userManager: UserManager
) : MediaRepository {

    override val genreList: List<String>
        get() = mediaManager.genreList

    override val genreListLastRetrieved: Long?
        get() = mediaManager.genreListLastRetrieved

    override val savedMediaData = HashMap<Int, MediaQuery.Media>()

    private val _mediaData = SingleLiveEvent<Resource<MediaQuery.Data>>()
    override val mediaData: LiveData<Resource<MediaQuery.Data>>
        get() = _mediaData

    private val _mediaStatus = SingleLiveEvent<Resource<MediaStatusQuery.Data>>()
    override val mediaStatus: LiveData<Resource<MediaStatusQuery.Data>>
        get() = _mediaStatus

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

    @SuppressLint("CheckResult")
    override fun getMedia(id: Int) {
        _mediaData.postValue(Resource.Loading())

        mediaDataSource.getMedia(id).subscribeWith(object : Observer<Response<MediaQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MediaQuery.Data>) {
                if (t.hasErrors()) {
                    _mediaData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    savedMediaData[t.data()?.Media()?.id()!!] = t.data()?.Media()!!
                    _mediaData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _mediaData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun checkMediaStatus(mediaId: Int) {
        mediaDataSource.checkMediaStatus(userManager.viewerData?.id!!,  mediaId).subscribeWith(object : Observer<Response<MediaStatusQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<MediaStatusQuery.Data>) {
                if (t.hasErrors()) {
                    _mediaStatus.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _mediaStatus.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _mediaStatus.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }
}