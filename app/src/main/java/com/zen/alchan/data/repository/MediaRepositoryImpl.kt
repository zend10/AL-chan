package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.MediaDataSource
import com.zen.alchan.data.localstorage.MediaManager
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaTagCollection
import com.zen.alchan.data.response.SeasonalAnime
import com.zen.alchan.helper.enums.SeasonalCategory
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.MediaCharacters
import com.zen.alchan.helper.utils.AndroidUtility
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.*
import java.util.*

class MediaRepositoryImpl(private val mediaDataSource: MediaDataSource,
                          private val mediaManager: MediaManager,
                          private val userManager: UserManager
) : MediaRepository {

    override val genreList: List<String?>
        get() = mediaManager.genreList

    override val genreListLastRetrieved: Long?
        get() = mediaManager.genreListLastRetrieved

    override val tagList: List<MediaTagCollection>
        get() = mediaManager.tagList

    override val tagListLastRetrieved: Long?
        get() = mediaManager.tagListLastRetrieved

    private val _mediaData = SingleLiveEvent<Resource<MediaQuery.Data>>()
    override val mediaData: LiveData<Resource<MediaQuery.Data>>
        get() = _mediaData

    private val _mediaStatus = SingleLiveEvent<Resource<MediaStatusQuery.Data>>()
    override val mediaStatus: LiveData<Resource<MediaStatusQuery.Data>>
        get() = _mediaStatus

    private val _mediaOverviewData = SingleLiveEvent<Resource<MediaOverviewQuery.Data>>()
    override val mediaOverviewData: LiveData<Resource<MediaOverviewQuery.Data>>
        get() = _mediaOverviewData

    private val _mediaCharactersData = SingleLiveEvent<Resource<MediaCharactersQuery.Data>>()
    override val mediaCharactersData: LiveData<Resource<MediaCharactersQuery.Data>>
        get() = _mediaCharactersData

    private val _mediaStaffsData = SingleLiveEvent<Resource<MediaStaffsQuery.Data>>()
    override val mediaStaffsData: LiveData<Resource<MediaStaffsQuery.Data>>
        get() = _mediaStaffsData

    private val _mediaStatsData = SingleLiveEvent<Resource<MediaStatsQuery.Data>>()
    override val mediaStatsData: LiveData<Resource<MediaStatsQuery.Data>>
        get() = _mediaStatsData

    private val _mediaReviewsData = SingleLiveEvent<Resource<MediaReviewsQuery.Data>>()
    override val mediaReviewsData: LiveData<Resource<MediaReviewsQuery.Data>>
        get() = _mediaReviewsData

    private val _trendingAnimeData = SingleLiveEvent<Resource<TrendingMediaQuery.Data>>()
    override val trendingAnimeData: LiveData<Resource<TrendingMediaQuery.Data>>
        get() = _trendingAnimeData

    private val _trendingMangaData = SingleLiveEvent<Resource<TrendingMediaQuery.Data>>()
    override val trendingMangaData: LiveData<Resource<TrendingMediaQuery.Data>>
        get() = _trendingMangaData

    private val _releasingTodayData = SingleLiveEvent<Resource<ReleasingTodayQuery.Data>>()
    override val releasingTodayData: LiveData<Resource<ReleasingTodayQuery.Data>>
        get() = _releasingTodayData

    @SuppressLint("CheckResult")
    override fun getGenre() {
        mediaDataSource.getGenre().subscribeWith(object : Observer<Response<GenreQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }
            override fun onNext(t: Response<GenreQuery.Data>) {
                if (!t.hasErrors() && !t.data?.genreCollection.isNullOrEmpty()) {
                    mediaManager.setGenreList(t.data?.genreCollection!!)
                }
            }
            override fun onError(e: Throwable) { e.printStackTrace() }
            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getTag() {
        mediaDataSource.getTag().subscribeWith(object : Observer<Response<TagQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }
            override fun onNext(t: Response<TagQuery.Data>) {
                if (!t.hasErrors() && !t.data?.mediaTagCollection.isNullOrEmpty()) {
                    val mediaTagCollections = Converter.convertMediaTagCollection(t.data?.mediaTagCollection!!)
                    mediaManager.setTagList(mediaTagCollections)
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
                    _mediaData.postValue(Resource.Error(t.errors!![0].message))
                } else {
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
                    _mediaStatus.postValue(Resource.Error(t.errors!![0].message))
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

    @SuppressLint("CheckResult")
    override fun getMediaOverview(id: Int) {
        _mediaOverviewData.postValue(Resource.Loading())

        mediaDataSource.getMediaOverview(id).subscribeWith(object : Observer<Response<MediaOverviewQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MediaOverviewQuery.Data>) {
                if (t.hasErrors()) {
                    _mediaOverviewData.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _mediaOverviewData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _mediaOverviewData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getMediaCharacters(id: Int, page: Int) {
        mediaDataSource.getMediaCharacters(id, page).subscribeWith(object : Observer<Response<MediaCharactersQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<MediaCharactersQuery.Data>) {
                if (t.hasErrors()) {
                    _mediaCharactersData.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _mediaCharactersData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _mediaCharactersData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getMediaStaffs(id: Int, page: Int) {
        mediaDataSource.getMediaStaffs(id, page).subscribeWith(object : Observer<Response<MediaStaffsQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<MediaStaffsQuery.Data>) {
                if (t.hasErrors()) {
                    _mediaStaffsData.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _mediaStaffsData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _mediaStaffsData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getMediaStats(id: Int) {
        _mediaStatsData.postValue(Resource.Loading())

        mediaDataSource.getMediaStats(id).subscribeWith(object : Observer<Response<MediaStatsQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<MediaStatsQuery.Data>) {
                if (t.hasErrors()) {
                    _mediaStatsData.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _mediaStatsData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _mediaStatsData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getMediaReviews(id: Int, page: Int, sort: List<ReviewSort>) {
        _mediaReviewsData.postValue(Resource.Loading())

        mediaDataSource.getMediaReviews(id, page, sort).subscribeWith(object : Observer<Response<MediaReviewsQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<MediaReviewsQuery.Data>) {
                if (t.hasErrors()) {
                    _mediaReviewsData.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _mediaReviewsData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _mediaReviewsData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getTrendingAnime() {
        _trendingAnimeData.postValue(Resource.Loading())

        mediaDataSource.getTrendingMedia(MediaType.ANIME).subscribeWith(object : Observer<Response<TrendingMediaQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<TrendingMediaQuery.Data>) {
                if (t.hasErrors()) {
                    _trendingAnimeData.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _trendingAnimeData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _trendingAnimeData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getTrendingManga() {
        _trendingMangaData.postValue(Resource.Loading())

        mediaDataSource.getTrendingMedia(MediaType.MANGA).subscribeWith(object : Observer<Response<TrendingMediaQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<TrendingMediaQuery.Data>) {
                if (t.hasErrors()) {
                    _trendingMangaData.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _trendingMangaData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _trendingMangaData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getReleasingToday(page: Int) {
        _releasingTodayData.postValue(Resource.Loading())

        mediaDataSource.getReleasingToday(page).subscribeWith(object : Observer<Response<ReleasingTodayQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<ReleasingTodayQuery.Data>) {
                if (t.hasErrors()) {
                    _releasingTodayData.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _releasingTodayData.postValue(Resource.Success(t.data()!!))
                }
            }

            override fun onError(e: Throwable) {
                _releasingTodayData.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }
}