package com.zen.alchan.ui.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.NotificationCategory
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.secondsToDateTime
import fragment.*
import kotlinx.android.synthetic.main.list_notification.view.*
import type.MediaType

class NotificationRvAdapter(private val context: Context,
                            private val list: List<NotificationsQuery.Notification?>,
                            private val listener: NotificationListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface NotificationListener {
        fun openUserPage(userId: Int)
        fun openActivityDetail(activityId: Int)
        fun openMediaPage(mediaId: Int, mediaType: MediaType)
        fun openThread(threadId: Int, siteUrl: String)
        fun openThreadReply(threadReplyId: Int, siteUrl: String)
    }

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_notification, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]
            when (item?.__typename) {
                NotificationCategory.AIRING_NOTIFICATION.value -> handleAiringNotification(item.fragments.onAiringNotification!!, holder)

                NotificationCategory.FOLLOWING_NOTIFICATION.value -> handleFollowingNotification(item.fragments.onFollowingNotification!!, holder)

                NotificationCategory.ACTIVITY_MESSAGE_NOTIFICATION.value -> {
                    val notif = item.fragments.onActivityMessageNotification!!
                    val activityNotification = ActivityNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.activityId)
                    handleActivityNotification(activityNotification, holder)
                }
                NotificationCategory.ACTIVITY_MENTION_NOTIFICATION.value -> {
                    val notif = item.fragments.onActivityMentionNotification!!
                    val activityNotification = ActivityNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.activityId)
                    handleActivityNotification(activityNotification, holder)
                }
                NotificationCategory.ACTIVITY_REPLY_NOTIFICATION.value -> {
                    val notif = item.fragments.onActivityReplyNotification!!
                    val activityNotification = ActivityNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.activityId)
                    handleActivityNotification(activityNotification, holder)
                }
                NotificationCategory.ACTIVITY_REPLY_SUBSCRIBED_NOTIFICATION.value -> {
                    val notif = item.fragments.onActivityReplySubscribedNotification!!
                    val activityNotification = ActivityNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.activityId)
                    handleActivityNotification(activityNotification, holder)
                }
                NotificationCategory.ACTIVITY_LIKE_NOTIFICATION.value -> {
                    val notif = item.fragments.onActivityLikeNotification!!
                    val activityNotification = ActivityNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.activityId)
                    handleActivityNotification(activityNotification, holder)
                }
                NotificationCategory.ACTIVITY_REPLY_LIKE_NOTIFICATION.value -> {
                    val notif = item.fragments.onActivityReplyLikeNotification!!
                    val activityNotification = ActivityNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.activityId)
                    handleActivityNotification(activityNotification, holder)
                }

                NotificationCategory.THREAD_COMMENT_MENTION_NOTIFICATION.value -> {
                    val notif = item.fragments.onThreadCommentMentionNotification!!
                    val threadReplyNotification = ThreadReplyNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.thread?.id, notif.thread?.title, notif.thread?.siteUrl, notif.commentId, notif.comment?.siteUrl)
                    handleThreadReplyNotification(threadReplyNotification, holder)
                }
                NotificationCategory.THREAD_COMMENT_REPLY_NOTIFICATION.value -> {
                    val notif = item.fragments.onThreadCommentReplyNotification!!
                    val threadReplyNotification = ThreadReplyNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.thread?.id, notif.thread?.title, notif.thread?.siteUrl, notif.commentId, notif.comment?.siteUrl)
                    handleThreadReplyNotification(threadReplyNotification, holder)
                }
                NotificationCategory.THREAD_COMMENT_SUBSCRIBED_NOTIFICATION.value -> {
                    val notif = item.fragments.onThreadCommentSubscribedNotification!!
                    val threadReplyNotification = ThreadReplyNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.thread?.id, notif.thread?.title, notif.thread?.siteUrl, notif.commentId, notif.comment?.siteUrl)
                    handleThreadReplyNotification(threadReplyNotification, holder)
                }
                NotificationCategory.THREAD_COMMENT_LIKE_NOTIFICATION.value -> {
                    val notif = item.fragments.onThreadCommentLikeNotification!!
                    val threadReplyNotification = ThreadReplyNotification(notif.userId, notif.user?.name, notif.user?.avatar?.large, notif.createdAt, notif.context, notif.thread?.id, notif.thread?.title, notif.thread?.siteUrl, notif.commentId, notif.comment?.siteUrl)
                    handleThreadReplyNotification(threadReplyNotification, holder)
                }

                NotificationCategory.THREAD_LIKE_NOTIFICATION.value -> handleThreadNotification(item.fragments.onThreadLikeNotification!!, holder)

                NotificationCategory.RELATED_MEDIA_ADDITION_NOTIFICATION.value -> handleRelationNotification(item.fragments.onRelatedMediaAdditionNotification!!, holder)
            }
        }
    }

    private fun handleAiringNotification(item: OnAiringNotification, holder: ItemViewHolder) {
        GlideApp.with(context).load(item.media?.coverImage?.large).into(holder.notificationImage)

        holder.notificationImage.setOnClickListener {
            listener.openMediaPage(item.animeId, item.media?.type ?: MediaType.ANIME)
        }

        try {
            holder.notificationText.text = "${item.contexts!![0]}${item.episode}${item.contexts[1]}${item.media?.title?.userPreferred}${item.contexts[2]}"
        } catch (e: Exception) {
            holder.notificationText.text = context.getString(R.string.episode_n_of_x_aired, item.episode, item.media?.title?.userPreferred)
        }

        holder.notificationDateText.text = item.createdAt?.secondsToDateTime()

        holder.itemView.setOnClickListener {
            listener.openMediaPage(item.animeId, item.media?.type ?: MediaType.ANIME)
        }
    }

    private fun handleFollowingNotification(item: OnFollowingNotification, holder: ItemViewHolder) {
        GlideApp.with(context).load(item.user?.avatar?.large).into(holder.notificationImage)

        holder.notificationImage.setOnClickListener {
            listener.openUserPage(item.userId)
        }

        holder.notificationText.text = "${item.user?.name}${item.context}"

        holder.notificationDateText.text = item.createdAt?.secondsToDateTime()

        holder.itemView.setOnClickListener {
            listener.openUserPage(item.userId)
        }
    }

    private fun handleActivityNotification(item: ActivityNotification, holder: ItemViewHolder) {
        GlideApp.with(context).load(item.userAvatar).into(holder.notificationImage)

        holder.notificationImage.setOnClickListener {
            listener.openUserPage(item.userId)
        }

        holder.notificationText.text = "${item.userName}${item.context}"

        holder.notificationDateText.text = item.createdAt?.secondsToDateTime()

        holder.itemView.setOnClickListener {
            listener.openActivityDetail(item.activityId)
        }
    }

    private fun handleThreadReplyNotification(item: ThreadReplyNotification, holder: ItemViewHolder) {
        GlideApp.with(context).load(item.userAvatar).into(holder.notificationImage)

        holder.notificationImage.setOnClickListener {
            listener.openUserPage(item.userId)
        }

        holder.notificationText.text = "${item.userName}${item.context}${item.threadName}"

        holder.notificationDateText.text = item.createdAt?.secondsToDateTime()

        holder.itemView.setOnClickListener {
            listener.openThreadReply(item.threadReplyId, item.threadReplyUrl ?: "")
        }
    }

    private fun handleThreadNotification(item: OnThreadLikeNotification, holder: ItemViewHolder) {
        GlideApp.with(context).load(item.user?.avatar?.large).into(holder.notificationImage)

        holder.notificationImage.setOnClickListener {
            listener.openUserPage(item.userId)
        }

        holder.notificationText.text = "${item.user?.name}${item.context}${item.thread?.title}"

        holder.notificationDateText.text = item.createdAt?.secondsToDateTime()

        holder.itemView.setOnClickListener {
            listener.openThread(item.threadId, item.thread?.siteUrl ?: "")
        }
    }

    private fun handleRelationNotification(item: OnRelatedMediaAdditionNotification, holder: ItemViewHolder) {
        GlideApp.with(context).load(item.media?.coverImage?.large).into(holder.notificationImage)

        holder.notificationImage.setOnClickListener {
            listener.openMediaPage(item.mediaId, item.media?.type ?: MediaType.ANIME)
        }

        holder.notificationText.text = "${item.media?.title?.userPreferred}${item.context}"

        holder.notificationDateText.text = item.createdAt?.secondsToDateTime()

        holder.itemView.setOnClickListener {
            listener.openMediaPage(item.mediaId, item.media?.type ?: MediaType.ANIME)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val notificationImage = view.notificationImage!!
        val notificationText = view.notificationText!!
        val notificationDateText = view.notificationDateText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ActivityNotification(val userId: Int, val userName: String?, val userAvatar: String?, val createdAt: Int?, val context: String?, val activityId: Int)

    class ThreadReplyNotification(val userId: Int, val userName: String?, val userAvatar: String?, val createdAt: Int?, val context: String?, val threadId: Int?, val threadName: String?, val threadUrl: String?, val threadReplyId: Int, val threadReplyUrl: String?)
}