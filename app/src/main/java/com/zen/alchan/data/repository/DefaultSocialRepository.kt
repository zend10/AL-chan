package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.SocialDataSource
import com.zen.alchan.data.response.SocialData
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.pojo.NullableItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.ActivityType
import type.LikeableType

class DefaultSocialRepository(private val socialDataSource: SocialDataSource) : SocialRepository {

    private val _activityToBeEdited = BehaviorSubject.create<NullableItem<Activity>>()
    override val activityToBeEdited: Observable<NullableItem<Activity>>
        get() = _activityToBeEdited

    private val _newOrEditedActivity = PublishSubject.create<NullableItem<Activity>>()
    override val newOrEditedActivity: Observable<NullableItem<Activity>>
        get() = _newOrEditedActivity

    private val _replyToBeEdited = BehaviorSubject.create<NullableItem<ActivityReply>>()
    override val replyToBeEdited: Observable<NullableItem<ActivityReply>>
        get() = _replyToBeEdited

    private val _newOrEditedReply = PublishSubject.create<NullableItem<ActivityReply>>()
    override val newOrEditedReply: Observable<NullableItem<ActivityReply>>
        get() = _newOrEditedReply

    override fun updateActivityToBeEdited(activity: Activity) {
        _activityToBeEdited.onNext(NullableItem(activity))
    }

    override fun clearActivityToBeEdited() {
        _activityToBeEdited.onNext(NullableItem(null))
    }

    override fun clearNewOrEditedActivity() {
        _newOrEditedActivity.onNext(NullableItem(null))
    }

    override fun updateReplyToBeEdited(activityReply: ActivityReply) {
        _replyToBeEdited.onNext(NullableItem(activityReply))
    }

    override fun clearReplyToBeEdited() {
        _replyToBeEdited.onNext(NullableItem(null))
    }

    override fun clearNewOrEditedReply() {
        _replyToBeEdited.onNext(NullableItem(null))
    }

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
        typeIn: List<ActivityType>?,
        isFollowing: Boolean?
    ): Observable<Page<Activity>> {
        return socialDataSource.getActivityList(page, userId, typeIn, isFollowing).map {
            it.data?.convert()
        }
    }

    override fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable {
        return socialDataSource.toggleActivitySubscription(id, isSubscribe)
    }

    override fun toggleLike(id: Int, likeableType: LikeableType): Completable {
        return socialDataSource.toggleLike(id, likeableType)
    }

    override fun deleteActivity(id: Int): Completable {
        return socialDataSource.deleteActivity(id)
    }

    override fun deleteActivityReply(id: Int): Completable {
        return socialDataSource.deleteActivityReply(id)
    }

    override fun saveTextActivity(id: Int?, text: String): Observable<TextActivity> {
        return socialDataSource.saveTextActivity(id, text).map {
            val textActivity = it.data?.saveTextActivity?.fragments?.onTextActivity?.convert()
            _newOrEditedActivity.onNext(NullableItem(textActivity))
            textActivity
        }
    }

    override fun saveActivityReply(id: Int?, activityId: Int, text: String): Observable<ActivityReply> {
        return socialDataSource.saveActivityReply(id, activityId, text).map {
            val activityReply = it.data?.saveActivityReply?.fragments?.activityReply?.convert()
            _newOrEditedReply.onNext(NullableItem(activityReply))
            activityReply
        }
    }

    override fun saveMessageActivity(
        id: Int?,
        recipientId: Int,
        message: String,
        private: Boolean
    ): Observable<MessageActivity> {
        return socialDataSource.saveMessageActivity(id, recipientId, message, private).map {
            val messageActivity = it.data?.saveMessageActivity?.fragments?.onMessageActivity?.convert()
            _newOrEditedActivity.onNext(NullableItem(messageActivity))
            messageActivity
        }
    }
}