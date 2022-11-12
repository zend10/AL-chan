package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.SocialDataSource
import com.zen.alchan.data.response.SocialData
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.Page
import io.reactivex.Completable
import io.reactivex.Observable
import type.ActivityType
import type.LikeableType

class DefaultSocialRepository(private val socialDataSource: SocialDataSource) : SocialRepository {

    override fun getSocialData(): Observable<SocialData> {
        return socialDataSource.getSocialData().map {
            it.data?.convert()
        }
    }

    override fun getActivityDetail(id: Int): Observable<Activity> {
        return socialDataSource.getActivityDetail(id).map {
            it.data?.convert()
        }
    }

    override fun getActivityList(
        page: Int,
        userId: Int?,
        type: ActivityType?,
        isFollowing: Boolean?
    ): Observable<Page<Activity>> {
        return socialDataSource.getActivityList(page, userId, type, isFollowing).map {
            it.data?.convert()
        }
    }

    override fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable {
        return socialDataSource.toggleActivitySubscription(id, isSubscribe)
    }

    override fun toggleLike(id: Int, likeableType: LikeableType): Completable {
        return socialDataSource.toggleLike(id, likeableType)
    }
}