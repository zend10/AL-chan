package com.zen.alchan.data.repository

import com.zen.alchan.data.response.SocialData
import io.reactivex.Completable
import io.reactivex.Observable
import type.LikeableType

interface SocialRepository {
    fun getSocialData(): Observable<SocialData>
    fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable
    fun toggleLike(id: Int, likeableType: LikeableType): Completable
}