package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.BrowseDataSource
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.MediaSort
import type.MediaType

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

    private val _staffBioData = SingleLiveEvent<Resource<StaffBioQuery.Data>>()
    override val staffBioData: LiveData<Resource<StaffBioQuery.Data>>
        get() = _staffBioData

    private val _staffCharacterData = SingleLiveEvent<Resource<StaffCharacterConnectionQuery.Data>>()
    override val staffCharacterData: LiveData<Resource<StaffCharacterConnectionQuery.Data>>
        get() = _staffCharacterData

    private val _staffAnimeData = SingleLiveEvent<Resource<StaffMediaConnectionQuery.Data>>()
    override val staffAnimeData: LiveData<Resource<StaffMediaConnectionQuery.Data>>
        get() = _staffAnimeData

    private val _staffMangaData = SingleLiveEvent<Resource<StaffMediaConnectionQuery.Data>>()
    override val staffMangaData: LiveData<Resource<StaffMediaConnectionQuery.Data>>
        get() = _staffMangaData

    private val _staffIsFavoriteData = SingleLiveEvent<Resource<StaffIsFavoriteQuery.Data>>()
    override val staffIsFavoriteData: LiveData<Resource<StaffIsFavoriteQuery.Data>>
        get() = _staffIsFavoriteData

    private val _studioData = SingleLiveEvent<Resource<StudioQuery.Data>>()
    override val studioData: LiveData<Resource<StudioQuery.Data>>
        get() = _studioData

    private val _studioMediaData = SingleLiveEvent<Resource<StudioMediaConnectionQuery.Data>>()
    override val studioMediaData: LiveData<Resource<StudioMediaConnectionQuery.Data>>
        get() = _studioMediaData

    private val _studioIsFavoriteData = SingleLiveEvent<Resource<StudioIsFavoriteQuery.Data>>()
    override val studioIsFavoriteData: LiveData<Resource<StudioIsFavoriteQuery.Data>>
        get() = _studioIsFavoriteData

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
    override fun getStaffBio(id: Int) {
        _staffBioData.postValue(Resource.Loading())

        browseDataSource.getStaffBio(id).subscribeWith(object : Observer<Response<StaffBioQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StaffBioQuery.Data>) {
                if (t.hasErrors()) {
                    _staffBioData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _staffBioData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _staffBioData.postValue(Resource.Error(e.localizedMessage))
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
    override fun getStaffAnime(id: Int, page: Int) {
        browseDataSource.getStaffMedia(id, MediaType.ANIME, page).subscribeWith(object : Observer<Response<StaffMediaConnectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StaffMediaConnectionQuery.Data>) {
                if (t.hasErrors()) {
                    _staffAnimeData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _staffAnimeData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _staffAnimeData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getStaffManga(id: Int, page: Int) {
        browseDataSource.getStaffMedia(id, MediaType.MANGA, page).subscribeWith(object : Observer<Response<StaffMediaConnectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StaffMediaConnectionQuery.Data>) {
                if (t.hasErrors()) {
                    _staffMangaData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _staffMangaData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _staffMangaData.postValue(Resource.Error(e.localizedMessage))
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

    @SuppressLint("CheckResult")
    override fun getStudio(id: Int) {
        browseDataSource.getStudio(id).subscribeWith(object : Observer<Response<StudioQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StudioQuery.Data>) {
                if (t.hasErrors()) {
                    _studioData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _studioData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _studioData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getStudioMedia(id: Int, page: Int, sort: MediaSort) {
        browseDataSource.getStudioMedia(id, page, sort).subscribeWith(object : Observer<Response<StudioMediaConnectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StudioMediaConnectionQuery.Data>) {
                if (t.hasErrors()) {
                    _studioMediaData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _studioMediaData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _studioMediaData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun checkStudioIsFavorite(id: Int) {
        browseDataSource.checkStudioIsFavorite(id).subscribeWith(object : Observer<Response<StudioIsFavoriteQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<StudioIsFavoriteQuery.Data>) {
                if (t.hasErrors()) {
                    _studioIsFavoriteData.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    _studioIsFavoriteData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _studioIsFavoriteData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }
}