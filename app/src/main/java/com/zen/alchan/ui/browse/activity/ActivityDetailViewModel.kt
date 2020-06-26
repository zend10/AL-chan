package com.zen.alchan.ui.browse.activity

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.User
import com.zen.alchan.data.response.UserAvatar
import com.zen.alchan.helper.pojo.ActivityItem
import com.zen.alchan.helper.pojo.ActivityReply
import type.LikeableType

class ActivityDetailViewModel(private val socialRepository: SocialRepository,
                              private val userRepository: UserRepository,
                              val gson: Gson) : ViewModel() {

    val textActivityText: String
        get() = socialRepository.textActivityText

    val listActivityText: String
        get() = socialRepository.listActivityText

    val messageActivityText: String
        get() = socialRepository.messageActivityText

    var activityId: Int? = null
    var activityDetail: ActivityItem? = null

    val userId: Int
        get() = userRepository.currentUser?.id!!

    val activityDetailResponse by lazy {
        socialRepository.activityDetailResponse
    }

    val toggleLikeDetailResponse by lazy {
        socialRepository.toggleLikeDetailResponse
    }

    val toggleActivitySubscriptionDetailResponse by lazy {
        socialRepository.toggleActivitySubscriptionDetailResponse
    }

    val deleteActivityDetailResponse by lazy {
        socialRepository.deleteActivityDetailResponse
    }

    val deleteActivityReplyResponse by lazy {
        socialRepository.deleteActivityReplyResponse
    }

    fun getActivityDetail() {
        if (activityId != null) {
            socialRepository.getActivityDetail(activityId!!)
        }
    }

    fun getReplies(activityType: String?, item: ActivityDetailQuery.Activity.Fragments?): ArrayList<ActivityReply> {
        val list = ArrayList<ActivityReply>()

        when (activityType) {
            textActivityText -> {
                item?.onTextActivity?.replies?.forEach { reply ->
                    val likeUser = User(
                        id = reply?.user?.id!!,
                        name = reply.user.name,
                        avatar = UserAvatar(null, reply.user.avatar?.medium)
                    )

                    val likes = ArrayList<User>()
                    reply.likes?.forEach { like ->
                        likes.add(
                            User(
                                id = like?.id!!,
                                name = like.name,
                                avatar = UserAvatar(null, like.avatar?.medium)
                            )
                        )
                    }

                    list.add(
                        ActivityReply(
                            reply.id,
                            reply.userId,
                            reply.activityId,
                            reply.text,
                            reply.likeCount,
                            reply.isLiked,
                            reply.createdAt,
                            likeUser,
                            likes
                        )
                    )
                }
            }
            listActivityText -> {
                item?.onListActivity?.replies?.forEach { reply ->
                    val likeUser = User(
                        id = reply?.user?.id!!,
                        name = reply.user.name,
                        avatar = UserAvatar(null, reply.user.avatar?.medium)
                    )

                    val likes = ArrayList<User>()
                    reply.likes?.forEach { like ->
                        likes.add(
                            User(
                                id = like?.id!!,
                                name = like.name,
                                avatar = UserAvatar(null, like.avatar?.medium)
                            )
                        )
                    }

                    list.add(
                        ActivityReply(
                            reply.id,
                            reply.userId,
                            reply.activityId,
                            reply.text,
                            reply.likeCount,
                            reply.isLiked,
                            reply.createdAt,
                            likeUser,
                            likes
                        )
                    )
                }
            }
            messageActivityText -> {
                item?.onMessageActivity?.replies?.forEach { reply ->
                    val likeUser = User(
                        id = reply?.user?.id!!,
                        name = reply.user.name,
                        avatar = UserAvatar(null, reply.user.avatar?.medium)
                    )

                    val likes = ArrayList<User>()
                    reply.likes?.forEach { like ->
                        likes.add(
                            User(
                                id = like?.id!!,
                                name = like.name,
                                avatar = UserAvatar(null, like.avatar?.medium)
                            )
                        )
                    }

                    list.add(
                        ActivityReply(
                            reply.id,
                            reply.userId,
                            reply.activityId,
                            reply.text,
                            reply.likeCount,
                            reply.isLiked,
                            reply.createdAt,
                            likeUser,
                            likes
                        )
                    )
                }
            }
        }

        return list
    }

    fun getLikes(activityType: String?, item: ActivityDetailQuery.Activity.Fragments?): List<User> {
        val list = ArrayList<User>()

        when (activityType) {
            textActivityText -> {
                item?.onTextActivity?.likes?.forEach { like ->
                    list.add(
                        User(
                            id = like?.id!!,
                            name = like.name,
                            avatar = UserAvatar(null, like.avatar?.medium)
                        )
                    )
                }
            }
            listActivityText -> {
                item?.onListActivity?.likes?.forEach { like ->
                    list.add(
                        User(
                            id = like?.id!!,
                            name = like.name,
                            avatar = UserAvatar(null, like.avatar?.medium)
                        )
                    )
                }
            }
            messageActivityText -> {
                item?.onMessageActivity?.likes?.forEach { like ->
                    list.add(
                        User(
                            id = like?.id!!,
                            name = like.name,
                            avatar = UserAvatar(null, like.avatar?.medium)
                        )
                    )
                }
            }
        }

        return list
    }

    fun toggleLike(id: Int, type: LikeableType) {
        socialRepository.toggleLike(id, type, true)
    }

    fun toggleSubscription(id: Int, subscribe: Boolean) {
        socialRepository.toggleActivitySubscription(id, subscribe, true)
    }

    fun deleteActivity(id: Int) {
        socialRepository.deleteActivity(id, true)
    }

    fun deleteActivityReply(id: Int) {
        socialRepository.deleteActivityReply(id)
    }

    fun notifyAllActivityList() {
        socialRepository.notifyAllActivityList()
    }
}