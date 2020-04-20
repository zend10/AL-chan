package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.BrowseDataSource
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class BrowseRepositoryImpl(private val browseDataSource: BrowseDataSource) : BrowseRepository {

    private val _characterData = SingleLiveEvent<Resource<CharacterQuery.Data>>()
    override val characterData: LiveData<Resource<CharacterQuery.Data>>
        get() = _characterData

    private val _characterMediaData = SingleLiveEvent<Resource<CharacterMediaConnectionQuery.Data>>()
    override val characterMediaData: LiveData<Resource<CharacterMediaConnectionQuery.Data>>
        get() = _characterMediaData

    private val _characterIsFavoriteData = SingleLiveEvent<Resource<CharacterIsFavoriteQuery.Data>>()
    override val characterIsFavoriteData: LiveData<Resource<CharacterIsFavoriteQuery.Data>>
        get() = _characterIsFavoriteData

    @SuppressLint("CheckResult")
    override fun getCharacter(id: Int) {
        _characterData.postValue(Resource.Loading())

        browseDataSource.getCharacter(id).subscribeWith(object : Observer<Response<CharacterQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<CharacterQuery.Data>) {
                if (t.hasErrors()) {
                    _characterData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _characterData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _characterData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getCharacterMedia(id: Int, page: Int) {
        browseDataSource.getCharacterMedia(id, page).subscribeWith(object : Observer<Response<CharacterMediaConnectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<CharacterMediaConnectionQuery.Data>) {
                if (t.hasErrors()) {
                    _characterMediaData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _characterMediaData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _characterMediaData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun checkCharacterIsFavorite(id: Int) {
        browseDataSource.checkCharacterIsFavorite(id).subscribeWith(object : Observer<Response<CharacterIsFavoriteQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<CharacterIsFavoriteQuery.Data>) {
                if (t.hasErrors()) {
                    _characterIsFavoriteData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _characterIsFavoriteData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _characterIsFavoriteData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }
}