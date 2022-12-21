package com.zen.alchan.data.converter

import com.zen.alchan.data.response.SocialData
import com.zen.alchan.data.response.anilist.*
import fragment.ActivityReply
import fragment.ActivityUser
import fragment.OnListActivity
import fragment.OnMessageActivity
import fragment.OnTextActivity

fun SocialDataQuery.Data.convert(): SocialData {
    return SocialData(
        friendsActivities = friendsActivities?.activities?.filterNotNull()?.map { activity ->
            when (activity.__typename) {
                TextActivity::class.java.simpleName -> {
                    activity.fragments.onTextActivity?.convert() ?: TextActivity()
                }
                ListActivity::class.java.simpleName -> {
                    activity.fragments.onListActivity?.convert() ?: ListActivity()
                }
                else -> TextActivity()
            }
        } ?: listOf(),
        globalActivities = globalActivities?.activities?.filterNotNull()?.map { activity ->
            when (activity.__typename) {
                TextActivity::class.java.simpleName -> {
                    activity.fragments.onTextActivity?.convert() ?: TextActivity()
                }
                else -> TextActivity()
            }
        } ?: listOf()
    )
}

fun OnTextActivity.convert(): TextActivity {
    return TextActivity(
        id = id,
        userId = userId ?: 0,
        replyCount = replyCount,
        text = text ?: "",
        isLocked = isLocked ?: false,
        isSubscribed = isSubscribed ?: false,
        likeCount = likeCount,
        isLiked = isLiked ?: false,
        isPinned = isPinned ?: false,
        siteUrl = siteUrl ?: "",
        createdAt = createdAt,
        user = user?.fragments?.activityUser?.convert() ?: User(),
        replies = replies?.filterNotNull()?.map {
            it.fragments.activityReply.convert()
        } ?: listOf(),
        likes = likes?.filterNotNull()?.map {
            it.fragments.activityUser.convert()
        } ?: listOf()
    )
}

fun OnListActivity.convert() : ListActivity {
    return ListActivity(
        id = id,
        userId = userId ?: 0,
        replyCount = replyCount,
        status = status ?: "",
        progress = progress ?: "",
        isLocked = isLocked ?: false,
        isSubscribed = isSubscribed ?: false,
        likeCount = likeCount,
        isLiked = isLiked ?: false,
        isPinned = isPinned ?: false,
        siteUrl = siteUrl ?: "",
        createdAt = createdAt,
        user = user?.fragments?.activityUser?.convert() ?: User(),
        media = media?.let {
            Media(
                idAniList = it.id,
                idMal = it.idMal,
                title = MediaTitle(
                    romaji = it.title?.romaji ?: "",
                    english = it.title?.english ?: "",
                    native = it.title?.native_ ?: "",
                    userPreferred = it.title?.userPreferred ?: ""
                ),
                type = it.type,
                format = it.format,
                startDate = it.startDate?.let {
                    FuzzyDate(
                        year = it.year,
                        month = it.month,
                        day = it.day
                    )
                },
                episodes = it.episodes,
                duration = it.duration,
                chapters = it.chapters,
                volumes = it.volumes,
                coverImage = MediaCoverImage(
                    extraLarge = it.coverImage?.extraLarge ?: "",
                    large = it.coverImage?.large ?: "",
                    medium = it.coverImage?.medium ?: ""
                ),
                bannerImage = it.bannerImage ?: ""
            )
        } ?: Media(),
        replies = replies?.filterNotNull()?.map {
            it.fragments.activityReply.convert()
        } ?: listOf(),
        likes = likes?.filterNotNull()?.map {
            it.fragments.activityUser.convert()
        } ?: listOf()
    )
}

fun OnMessageActivity.convert() : MessageActivity {
    return MessageActivity(
        id = id,
        recipientId = recipientId ?: 0,
        messengerId = messengerId ?: 0,
        replyCount = replyCount,
        message = message ?: "",
        isLocked = isLocked ?: false,
        isSubscribed = isSubscribed ?: false,
        likeCount = likeCount,
        isLiked = isLiked ?: false,
        isPrivate = isPrivate ?: false,
        siteUrl = siteUrl ?: "",
        createdAt = createdAt,
        recipient = recipient?.fragments?.activityUser?.convert() ?: User(),
        messenger = messenger?.fragments?.activityUser?.convert() ?: User(),
        replies = replies?.filterNotNull()?.map {
            it.fragments.activityReply.convert()
        } ?: listOf(),
        likes = likes?.filterNotNull()?.map {
            it.fragments.activityUser.convert()
        } ?: listOf()
    )
}

private fun ActivityUser.convert() : User {
    return User(
        id = id,
        name = name,
        avatar = UserAvatar(
            large = avatar?.large ?: "",
            medium = avatar?.medium ?: ""
        ),
        isFollowing = isFollowing ?: false,
        isFollower = isFollower ?: false,
        donatorTier = donatorTier ?: 0,
        donatorBadge = donatorBadge ?: "",
        moderatorRoles = moderatorRoles?.filterNotNull() ?: listOf()
    )
}

fun ActivityReply.convert(): com.zen.alchan.data.response.anilist.ActivityReply {
    return com.zen.alchan.data.response.anilist.ActivityReply(
        id = id,
        userId = userId ?: 0,
        activityId = activityId ?: 0,
        text = text ?: "",
        likeCount = likeCount,
        isLiked = isLiked ?: false,
        createdAt = createdAt,
        user =  user?.fragments?.activityUser?.convert() ?: User(),
        likes = likes?.filterNotNull()?.map {
            it.fragments.activityUser.convert()
        } ?: listOf()
    )
}