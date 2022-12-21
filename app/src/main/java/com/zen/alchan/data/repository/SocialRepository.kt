package com.zen.alchan.data.repository

import com.zen.alchan.data.response.SocialData
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.pojo.NullableItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import type.ActivityType
import type.LikeableType

interface SocialRepository {
    val activityToBeEdited: Observable<NullableItem<Activity>>
    val newOrEditedActivity: Observable<NullableItem<Activity>>
    val replyToBeEdited: Observable<NullableItem<ActivityReply>>
    val newOrEditedReply: Observable<NullableItem<ActivityReply>>
    fun updateActivityToBeEdited(activity: Activity)
    fun clearActivityToBeEdited()
    fun clearNewOrEditedActivity()
    fun updateReplyToBeEdited(activityReply: ActivityReply)
    fun clearReplyToBeEdited()
    fun clearNewOrEditedReply()
    fun getSocialData(): Observable<SocialData>
    fun getActivityDetail(id: Int): Observable<Activity>
    fun getActivityList(page: Int, userId: Int?, typeIn: List<ActivityType>?, isFollowing: Boolean?): Observable<Page<Activity>>
    fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable
    fun toggleLike(id: Int, likeableType: LikeableType): Completable
    fun deleteActivity(id: Int): Completable
    fun deleteActivityReply(id: Int): Completable
    fun saveTextActivity(id: Int?, text: String): Observable<TextActivity>
    fun saveActivityReply(id: Int?, activityId: Int, text: String): Observable<ActivityReply>
    fun saveMessageActivity(id: Int?, recipientId: Int, message: String, private: Boolean): Observable<MessageActivity>
}