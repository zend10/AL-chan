package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
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
}