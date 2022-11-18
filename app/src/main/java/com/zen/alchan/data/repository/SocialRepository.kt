package com.zen.alchan.data.repository

import com.zen.alchan.data.response.SocialData
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.Page
import io.reactivex.Completable
import io.reactivex.Observable
import type.ActivityType
import type.LikeableType

interface SocialRepository {
    fun getSocialData(): Observable<SocialData>
    fun getActivityDetail(id: Int): Observable<Activity>
    fun getActivityList(page: Int, userId: Int?, typeIn: List<ActivityType>?, isFollowing: Boolean?): Observable<Page<Activity>>
    fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable
    fun toggleLike(id: Int, likeableType: LikeableType): Completable
    fun deleteActivity(id: Int): Completable
    fun deleteActivityReply(id: Int): Completable
}