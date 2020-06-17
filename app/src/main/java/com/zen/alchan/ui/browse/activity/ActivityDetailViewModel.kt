package com.zen.alchan.ui.browse.activity

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.User
import com.zen.alchan.data.response.UserAvatar
import com.zen.alchan.helper.pojo.ActivityItem
import com.zen.alchan.helper.pojo.ActivityReply

class ActivityDetailViewModel(private val socialRepository: SocialRepository,
                              private val userRepository: UserRepository) : ViewModel() {

    val TEXT_ACTIVITY = "TextActivity"
    val LIST_ACTIVITY = "ListActivity"
    val MESSAGE_ACTIVITY = "MessageActivity"

    var activityId: Int? = null
    var activityDetail: ActivityItem? = null

    val userId: Int
        get() = userRepository.viewerData.value?.id!!

    val activityDetailResponse by lazy {
        socialRepository.activityDetailResponse
    }

    fun getActivityDetail() {
        if (activityId != null) {
            socialRepository.getActivityDetail(activityId!!)
        }
    }

    fun getReplies(activityType: String?, item: ActivityDetailQuery.Activity.Fragments?): List<ActivityReply> {
        val list = ArrayList<ActivityReply>()

        when (activityType) {
            TEXT_ACTIVITY -> {
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
            LIST_ACTIVITY -> {
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
            MESSAGE_ACTIVITY -> {
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
            TEXT_ACTIVITY -> {
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
            LIST_ACTIVITY -> {
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
            MESSAGE_ACTIVITY -> {
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
}