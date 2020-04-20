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

    private val _staffData = SingleLiveEvent<Resource<StaffQuery.Data>>()
    override val staffData: LiveData<Resource<StaffQuery.Data>>
        get() = _staffData

    private val _staffCharacterData = SingleLiveEvent<Resource<StaffCharacterConnectionQuery.Data>>()
    override val staffCharacterData: LiveData<Resource<StaffCharacterConnectionQuery.Data>>
        get() = _staffCharacterData

    private val _staffMediaData = SingleLiveEvent<Resource<StaffMediaConnectionQuery.Data>>()
    override val staffMediaData: LiveData<Resource<StaffMediaConnectionQuery.Data>>
        get() = _staffMediaData

    private val _staffIsFavoriteData = SingleLiveEvent<Resource<StaffIsFavoriteQuery.Data>>()
    override val staffIsFavoriteData: LiveData<Resource<StaffIsFavoriteQuery.Data>>
        get() = _staffIsFavoriteData

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

    @SuppressLint("CheckResult")
    override fun getStaff(id: Int) {
        _staffData.postValue(Resource.Loading())

        browseDataSource.getStaff(id).subscribeWith(object : Observer<Response<StaffQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StaffQuery.Data>) {
                if (t.hasErrors()) {
                    _staffData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _staffData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _staffData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getStaffCharacter(id: Int, page: Int) {
        browseDataSource.getStaffCharacter(id, page).subscribeWith(object : Observer<Response<StaffCharacterConnectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StaffCharacterConnectionQuery.Data>) {
                if (t.hasErrors()) {
                    _staffCharacterData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _staffCharacterData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _staffCharacterData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getStaffMedia(id: Int, page: Int) {
        browseDataSource.getStaffMedia(id, page).subscribeWith(object : Observer<Response<StaffMediaConnectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StaffMediaConnectionQuery.Data>) {
                if (t.hasErrors()) {
                    _staffMediaData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _staffMediaData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _staffMediaData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun checkStaffIsFavorite(id: Int) {
        browseDataSource.checkStaffIsFavorite(id).subscribeWith(object : Observer<Response<StaffIsFavoriteQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StaffIsFavoriteQuery.Data>) {
                if (t.hasErrors()) {
                    _staffIsFavoriteData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _staffIsFavoriteData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _staffIsFavoriteData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }
}