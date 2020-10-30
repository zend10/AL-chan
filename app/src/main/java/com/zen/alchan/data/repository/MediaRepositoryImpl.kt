package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.MediaDataSource
import com.zen.alchan.data.localstorage.MediaManager
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.*
import com.zen.alchan.helper.enums.SeasonalCategory
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.MediaCharacters
import com.zen.alchan.helper.utils.AndroidUtility
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.Single
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

    // Used for banner in Social tab, for aesthetic purpose
    private val _mostTrendingAnimeBannerLiveData = MutableLiveData<String?>()
    override val mostTrendingAnimeBannerLivaData: LiveData<String?>
        get() = _mostTrendingAnimeBannerLiveData

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

    private val _mediaFriendsMediaListData = SingleLiveEvent<Resource<MediaSocialQuery.Data>>()
    override val mediaFriendsMediaListData: LiveData<Resource<MediaSocialQuery.Data>>
        get() = _mediaFriendsMediaListData

    private val _mediaActivityData = SingleLiveEvent<Resource<MediaActivityQuery.Data>>()
    override val mediaActivityData: LiveData<Resource<MediaActivityQuery.Data>>
        get() = _mediaActivityData

    private val _trendingAnimeData = SingleLiveEvent<Resource<TrendingMediaQuery.Data>>()
    override val trendingAnimeData: LiveData<Resource<TrendingMediaQuery.Data>>
        get() = _trendingAnimeData

    private val _trendingMangaData = SingleLiveEvent<Resource<TrendingMediaQuery.Data>>()
    override val trendingMangaData: LiveData<Resource<TrendingMediaQuery.Data>>
        get() = _trendingMangaData

    private val _releasingTodayData = SingleLiveEvent<Resource<ReleasingTodayQuery.Data>>()
    override val releasingTodayData: LiveData<Resource<ReleasingTodayQuery.Data>>
        get() = _releasingTodayData

    private val _recentReviewsData = SingleLiveEvent<Resource<ReviewsQuery.Data>>()
    override val recentReviewsData: LiveData<Resource<ReviewsQuery.Data>>
        get() = _recentReviewsData

    private val _reviewsData = SingleLiveEvent<Resource<ReviewsQuery.Data>>()
    override val reviewsData: LiveData<Resource<ReviewsQuery.Data>>
        get() = _reviewsData

    private val _reviewDetailData = SingleLiveEvent<Resource<ReviewDetailQuery.Data>>()
    override val reviewDetailData: LiveData<Resource<ReviewDetailQuery.Data>>
        get() = _reviewDetailData

    private val _rateReviewResponse = SingleLiveEvent<Resource<RateReviewMutation.Data>>()
    override val rateReviewResponse: LiveData<Resource<RateReviewMutation.Data>>
        get() = _rateReviewResponse

    private val _checkReviewResponse = SingleLiveEvent<Resource<CheckReviewQuery.Data>>()
    override val checkReviewResponse: LiveData<Resource<CheckReviewQuery.Data>>
        get() = _checkReviewResponse

    private val _saveReviewResponse = SingleLiveEvent<Resource<SaveReviewMutation.Data>>()
    override val saveReviewResponse: LiveData<Resource<SaveReviewMutation.Data>>
        get() = _saveReviewResponse

    private val _deleteReviewResponse = SingleLiveEvent<Resource<Boolean>>()
    override val deleteReviewResponse: LiveData<Resource<Boolean>>
        get() = _deleteReviewResponse

    private val _animeDetailsLiveData = SingleLiveEvent<Resource<AnimeDetails>>()
    override val animeDetailsLiveData: LiveData<Resource<AnimeDetails>>
        get() = _animeDetailsLiveData

    private val _mangaDetailsLiveData = SingleLiveEvent<Resource<MangaDetails>>()
    override val mangaDetailsLiveData: LiveData<Resource<MangaDetails>>
        get() = _mangaDetailsLiveData

    private val _animeVideoLiveData = SingleLiveEvent<Resource<AnimeVideo>>()
    override val animeVideoLiveData: LiveData<Resource<AnimeVideo>>
        get() = _animeVideoLiveData

    private val _triggerMediaCharacter = SingleLiveEvent<Boolean>()
    override val triggerMediaCharacter: LiveData<Boolean>
        get() = _triggerMediaCharacter

    private val _triggerMediaStaff = SingleLiveEvent<Boolean>()
    override val triggerMediaStaff: LiveData<Boolean>
        get() = _triggerMediaStaff

    private val _triggerMediaReview = SingleLiveEvent<Boolean>()
    override val triggerMediaReview: LiveData<Boolean>
        get() = _triggerMediaReview

    private val _triggerMediaSocial = SingleLiveEvent<Boolean>()
    override val triggerMediaSocial: LiveData<Boolean>
        get() = _triggerMediaSocial

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
        mediaDataSource.getMedia(id).subscribeWith(AndroidUtility.rxApolloCallback(_mediaData))
    }

    @SuppressLint("CheckResult")
    override fun checkMediaStatus(mediaId: Int) {
        if (userManager.viewerData?.id == null) return
        mediaDataSource.checkMediaStatus(userManager.viewerData?.id!!,  mediaId).subscribeWith(AndroidUtility.rxApolloCallback(_mediaStatus))
    }

    @SuppressLint("CheckResult")
    override fun getMediaOverview(id: Int) {
        _mediaOverviewData.postValue(Resource.Loading())
        mediaDataSource.getMediaOverview(id).subscribeWith(AndroidUtility.rxApolloCallback(_mediaOverviewData))
    }

    @SuppressLint("CheckResult")
    override fun getMediaCharacters(id: Int, page: Int) {
        mediaDataSource.getMediaCharacters(id, page).subscribeWith(AndroidUtility.rxApolloCallback(_mediaCharactersData))
    }

    @SuppressLint("CheckResult")
    override fun getMediaStaffs(id: Int, page: Int) {
        mediaDataSource.getMediaStaffs(id, page).subscribeWith(AndroidUtility.rxApolloCallback(_mediaStaffsData))
    }

    @SuppressLint("CheckResult")
    override fun getMediaStats(id: Int) {
        _mediaStatsData.postValue(Resource.Loading())
        mediaDataSource.getMediaStats(id).subscribeWith(AndroidUtility.rxApolloCallback(_mediaStatsData))
    }

    @SuppressLint("CheckResult")
    override fun getMediaReviews(id: Int, page: Int, sort: List<ReviewSort>) {
        _mediaReviewsData.postValue(Resource.Loading())
        mediaDataSource.getMediaReviews(id, page, sort).subscribeWith(AndroidUtility.rxApolloCallback(_mediaReviewsData))
    }

    @SuppressLint("CheckResult")
    override fun getMediaFriendsMediaList(mediaId: Int, page: Int) {
        mediaDataSource.getMediaFriendsMediaList(mediaId, page).subscribeWith(AndroidUtility.rxApolloCallback(_mediaFriendsMediaListData))
    }

    @SuppressLint("CheckResult")
    override fun getMediaActivity(mediaId: Int, page: Int) {
        _mediaActivityData.postValue(Resource.Loading())
        mediaDataSource.getMediaActivity(mediaId, page).subscribeWith(AndroidUtility.rxApolloCallback(_mediaActivityData))
    }

    @SuppressLint("CheckResult")
    override fun getTrendingAnime() {
        _trendingAnimeData.postValue(Resource.Loading())
        _mostTrendingAnimeBannerLiveData.postValue(mediaManager.mostTrendingAnimeBanner)

        mediaDataSource.getTrendingMedia(MediaType.ANIME).subscribeWith(object : Observer<Response<TrendingMediaQuery.Data>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Response<TrendingMediaQuery.Data>) {
                if (t.hasErrors()) {
                    _trendingAnimeData.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _trendingAnimeData.postValue(Resource.Success(t.data!!))

                    if (t.data?.page?.media?.get(0)?.bannerImage != null) {
                        mediaManager.setMostTrendingAnimeBanner(t.data?.page?.media?.get(0)?.bannerImage)
                        _mostTrendingAnimeBannerLiveData.postValue(mediaManager.mostTrendingAnimeBanner)
                    }
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
        mediaDataSource.getTrendingMedia(MediaType.MANGA).subscribeWith(AndroidUtility.rxApolloCallback(_trendingMangaData))
    }

    @SuppressLint("CheckResult")
    override fun getReleasingToday(page: Int) {
        _releasingTodayData.postValue(Resource.Loading())
        mediaDataSource.getReleasingToday(page).subscribeWith(AndroidUtility.rxApolloCallback(_releasingTodayData))
    }

    @SuppressLint("CheckResult")
    override fun getReviews(
        page: Int,
        perPage: Int,
        mediaType: MediaType?,
        sort: List<ReviewSort>,
        isRecent: Boolean
    ) {
        mediaDataSource.getReviews(page, perPage, mediaType, sort).subscribeWith(AndroidUtility.rxApolloCallback(if (isRecent) _recentReviewsData else _reviewsData))
    }

    @SuppressLint("CheckResult")
    override fun getReviewDetail(reviewId: Int) {
        _reviewDetailData.postValue(Resource.Loading())
        mediaDataSource.getReviewDetail(reviewId).subscribeWith(AndroidUtility.rxApolloCallback(_reviewDetailData))
    }

    @SuppressLint("CheckResult")
    override fun rateReview(reviewId: Int, rating: ReviewRating) {
        _rateReviewResponse.postValue(Resource.Loading())
        mediaDataSource.rateReview(reviewId, rating).subscribeWith(AndroidUtility.rxApolloCallback(_rateReviewResponse))
    }

    @SuppressLint("CheckResult")
    override fun checkReview(mediaId: Int) {
        if (userManager.viewerData?.id == null) return
        _checkReviewResponse.postValue(Resource.Loading())
        mediaDataSource.checkReview(mediaId, userManager.viewerData?.id!!).subscribeWith(AndroidUtility.rxApolloCallback(_checkReviewResponse))
    }

    @SuppressLint("CheckResult")
    override fun saveReview(
        id: Int?,
        mediaId: Int,
        body: String,
        summary: String,
        score: Int,
        private: Boolean
    ) {
        _saveReviewResponse.postValue(Resource.Loading())
        mediaDataSource.saveReview(id, mediaId, body, summary, score, private).subscribeWith(AndroidUtility.rxApolloCallback(_saveReviewResponse))
    }

    @SuppressLint("CheckResult")
    override fun deleteReview(id: Int) {
        _deleteReviewResponse.postValue(Resource.Loading())
        mediaDataSource.deleteReview(id).subscribeWith(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                // do nothing
            }

            override fun onComplete() {
                _deleteReviewResponse.postValue(Resource.Success(true))
            }

            override fun onError(e: Throwable) {
                _deleteReviewResponse.postValue(Resource.Error(e.localizedMessage))
            }
        })
    }

    override fun getAnimeDetails(malId: Int) {
        mediaDataSource.getAnimeDetails(malId).enqueue(AndroidUtility.apiCallback(_animeDetailsLiveData))
    }

    override fun getMangaDetails(malId: Int) {
        mediaDataSource.getMangaDetails(malId).enqueue(AndroidUtility.apiCallback(_mangaDetailsLiveData))
    }

    override fun getAnimeVideos(malId: Int) {
        mediaDataSource.getAnimeVideos(malId).enqueue(AndroidUtility.apiCallback(_animeVideoLiveData))
    }

    override fun triggerRefreshMediaChildren() {
        _triggerMediaCharacter.postValue(true)
        _triggerMediaStaff.postValue(true)
        _triggerMediaReview.postValue(true)
        _triggerMediaSocial.postValue(true)
    }
}