package com.zen.alchan.data.datasource

import CheckReviewQuery
import DeleteReviewMutation
import GenreQuery
import MediaActivityQuery
import MediaCharactersQuery
import MediaOverviewQuery
import MediaQuery
import MediaReviewsQuery
import MediaSocialQuery
import MediaStaffsQuery
import MediaStatsQuery
import MediaStatusQuery
import RateReviewMutation
import ReleasingTodayQuery
import ReviewDetailQuery
import ReviewsQuery
import SaveReviewMutation
import TagQuery
import TrendingMediaQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import com.zen.alchan.data.network.service.JikanRestService
import com.zen.alchan.data.response.AnimeDetails
import com.zen.alchan.data.response.AnimeVideo
import com.zen.alchan.data.response.MangaDetails
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import type.*

class MediaDataSourceImpl(private val apolloHandler: ApolloHandler,
                          private val jikanRestService: JikanRestService
) : MediaDataSource {

    override fun getGenre(): Observable<Response<GenreQuery.Data>> {
        val query = GenreQuery()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getTag(): Observable<Response<TagQuery.Data>> {
        val query = TagQuery()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMedia(id: Int): Observable<Response<MediaQuery.Data>> {
        val query = MediaQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkMediaStatus(
        userId: Int,
        mediaId: Int
    ): Observable<Response<MediaStatusQuery.Data>> {
        val query = MediaStatusQuery(userId = Input.fromNullable(userId), mediaId = Input.fromNullable(mediaId))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaOverview(id: Int): Observable<Response<MediaOverviewQuery.Data>> {
        val query = MediaOverviewQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaCharacters(
        id: Int,
        page: Int
    ): Observable<Response<MediaCharactersQuery.Data>> {
        val query = MediaCharactersQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaStaffs(id: Int, page: Int): Observable<Response<MediaStaffsQuery.Data>> {
        val query = MediaStaffsQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaStats(id: Int): Observable<Response<MediaStatsQuery.Data>> {
        val query = MediaStatsQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaReviews(
        id: Int,
        page: Int,
        sort: List<ReviewSort>
    ): Observable<Response<MediaReviewsQuery.Data>> {
        val query = MediaReviewsQuery(
            id = Input.fromNullable(id),
            page = Input.fromNullable(page),
            sort = Input.fromNullable(sort)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaFriendsMediaList(
        mediaId: Int,
        page: Int
    ): Observable<Response<MediaSocialQuery.Data>> {
        val query = MediaSocialQuery(
            mediaId = Input.fromNullable(mediaId),
            page = Input.fromNullable(page)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaActivity(
        mediaId: Int,
        page: Int
    ): Observable<Response<MediaActivityQuery.Data>> {
        val query = MediaActivityQuery(
            mediaId = Input.fromNullable(mediaId),
            page = Input.fromNullable(page)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getTrendingMedia(type: MediaType): Observable<Response<TrendingMediaQuery.Data>> {
        val query = TrendingMediaQuery(type = Input.fromNullable(type))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getReleasingToday(page: Int): Observable<Response<ReleasingTodayQuery.Data>> {
        val query = ReleasingTodayQuery(page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getReviews(
        page: Int,
        perPage: Int,
        mediaType: MediaType?,
        sort: List<ReviewSort>
    ): Observable<Response<ReviewsQuery.Data>> {
        val query = ReviewsQuery(
            page = Input.fromNullable(page),
            perPage = Input.fromNullable(perPage),
            mediaType = Input.optional(mediaType),
            sort = Input.fromNullable(sort)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getReviewDetail(reviewId: Int): Observable<Response<ReviewDetailQuery.Data>> {
        val query = ReviewDetailQuery(id = Input.fromNullable(reviewId))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun rateReview(
        reviewId: Int,
        rating: ReviewRating
    ): Observable<Response<RateReviewMutation.Data>> {
        val mutation = RateReviewMutation(reviewId = Input.fromNullable(reviewId), rating = Input.fromNullable(rating))
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkReview(
        mediaId: Int,
        userId: Int
    ): Observable<Response<CheckReviewQuery.Data>> {
        val query = CheckReviewQuery(mediaId = Input.fromNullable(mediaId), userId = Input.fromNullable(userId))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun saveReview(
        id: Int?,
        mediaId: Int,
        body: String,
        summary: String,
        score: Int,
        private: Boolean
    ): Observable<Response<SaveReviewMutation.Data>> {
        val mutation = SaveReviewMutation(
            id = Input.optional(id),
            mediaId = Input.fromNullable(mediaId),
            body = Input.fromNullable(body),
            summary = Input.fromNullable(summary),
            score = Input.fromNullable(score),
            private_ = Input.fromNullable(private)
        )
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteReview(id: Int): Completable {
        val mutation = DeleteReviewMutation(id = Input.fromNullable(id))
        val mutationCall = apolloHandler.apolloClient.prefetch(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getAnimeDetails(malId: Int): Call<AnimeDetails> {
        return jikanRestService.getAnimeDetails(malId)
    }

    override fun getMangaDetails(malId: Int): Call<MangaDetails> {
        return jikanRestService.getMangaDetails(malId)
    }

    override fun getAnimeVideos(malId: Int): Call<AnimeVideo> {
        return jikanRestService.getAnimeVideos(malId)
    }
}