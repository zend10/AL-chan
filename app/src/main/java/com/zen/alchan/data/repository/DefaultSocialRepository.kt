package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.SocialDataSource
import com.zen.alchan.data.response.SocialData
import io.reactivex.Completable
import io.reactivex.Observable
import type.LikeableType

class DefaultSocialRepository(private val socialDataSource: SocialDataSource) : SocialRepository {

    override fun getSocialData(): Observable<SocialData> {
        return socialDataSource.getSocialData().map {
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