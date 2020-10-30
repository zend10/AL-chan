package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.zen.alchan.data.response.AnimeDetails
import com.zen.alchan.data.response.AnimeVideo
import com.zen.alchan.data.response.MangaDetails
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Call
import type.*

interface MediaDataSource {
    fun getGenre(): Observable<Response<GenreQuery.Data>>
    fun getTag(): Observable<Response<TagQuery.Data>>
    fun getMedia(id: Int): Observable<Response<MediaQuery.Data>>
    fun checkMediaStatus(userId: Int, mediaId: Int): Observable<Response<MediaStatusQuery.Data>>
    fun getMediaOverview(id: Int): Observable<Response<MediaOverviewQuery.Data>>
    fun getMediaCharacters(id: Int, page: Int): Observable<Response<MediaCharactersQuery.Data>>
    fun getMediaStaffs(id: Int, page: Int): Observable<Response<MediaStaffsQuery.Data>>
    fun getMediaStats(id: Int): Observable<Response<MediaStatsQuery.Data>>
    fun getMediaReviews(id: Int, page: Int, sort: List<ReviewSort>): Observable<Response<MediaReviewsQuery.Data>>
    fun getMediaFriendsMediaList(mediaId: Int, page: Int): Observable<Response<MediaSocialQuery.Data>>
    fun getMediaActivity(mediaId: Int, page: Int): Observable<Response<MediaActivityQuery.Data>>

    fun getTrendingMedia(type: MediaType): Observable<Response<TrendingMediaQuery.Data>>
    fun getReleasingToday(page: Int): Observable<Response<ReleasingTodayQuery.Data>>
    fun getReviews(page:Int, perPage: Int, mediaType: MediaType?, sort: List<ReviewSort>): Observable<Response<ReviewsQuery.Data>>

    fun getReviewDetail(reviewId: Int): Observable<Response<ReviewDetailQuery.Data>>
    fun rateReview(reviewId: Int, rating: ReviewRating): Observable<Response<RateReviewMutation.Data>>

    fun checkReview(mediaId: Int, userId: Int): Observable<Response<CheckReviewQuery.Data>>
    fun saveReview(id: Int?, mediaId: Int, body: String, summary: String, score: Int, private: Boolean): Observable<Response<SaveReviewMutation.Data>>
    fun deleteReview(id: Int): Completable

    fun getAnimeDetails(malId: Int): Call<AnimeDetails>
    fun getMangaDetails(malId: Int): Call<MangaDetails>
    fun getAnimeVideos(malId: Int): Call<AnimeVideo>
}