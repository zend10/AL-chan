package com.zen.alchan.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.databinding.ListLoadingBinding
import com.zen.alchan.databinding.ListNotificationBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.makeVisible
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class NotificationsAdapter(
    private val context: Context,
    list: List<Notification?>,
    private val appSetting: AppSetting,
    private val listener: NotificationsListener
) : BaseRecyclerViewAdapter<Notification?, ViewBinding>(list) {

    private var unreadNotificationCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = ListNotificationBinding.inflate(inflater, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    fun setUnreadNotificationCount(newUnreadNotificationCount: Int) {
        unreadNotificationCount = newUnreadNotificationCount
    }

    inner class ItemViewHolder(private val binding: ListNotificationBinding) : ViewHolder(binding) {
        override fun bind(item: Notification?, index: Int) {
            if (item == null)
                return

            when (item) {
                is AiringNotification -> handleAiringNotification(item)
                is FollowingNotification -> handleFollowingNotification(item)
                is ActivityMessageNotification -> handleActivityMessageNotification(item)
                is ActivityMentionNotification -> handleActivityMentionNotification(item)
                is ActivityReplyNotification -> handleActivityReplyNotification(item)
                is ActivityReplySubscribedNotification -> handleActivityReplySubscribedNotification(item)
                is ActivityLikeNotification -> handleActivityLikeNotification(item)
                is ActivityReplyLikeNotification -> handleActivityReplyLikeNotification(item)
                is ThreadCommentMentionNotification -> handleThreadCommentMentionNotification(item)
                is ThreadCommentReplyNotification -> handleThreadCommentReplyNotification(item)
                is ThreadCommentSubscribedNotification -> handleThreadCommentSubscribedNotification(item)
                is ThreadCommentLikeNotification -> handleThreadCommentLikeNotification(item)
                is ThreadLikeNotification -> handleThreadLikeNotification(item)
                is RelatedMediaAdditionNotification -> handleRelatedMediaAdditionNotification(item)
                is MediaDataChangeNotification -> handleMediaDataChangeNotification(item)
                is MediaMergeNotification -> handleMediaMergeNotification(item)
                is MediaDeletionNotification -> handleMediaDeletionNotification(item)
                else -> {
                    binding.notificationsText.text = ""
                    ImageUtil.loadImage(context, 0, binding.notificationImage)
                    binding.root.isClickable = false
                }
            }

            binding.notificationsText.text = item.getMessage(appSetting)
            binding.notificationDate.text = TimeUtil.displayInDayDateTimeFormat(item.createdAt)
            binding.notificationUnreadOverlay.show(index < unreadNotificationCount)
        }

        private fun handleAiringNotification(notification: AiringNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.media.getCoverImage(appSetting), notificationImage)
                notificationViewDetail.makeVisible(false)
                root.clicks {
                    listener.navigateToMedia(notification.media)
                }
                root.isClickable = true
            }
        }

        private fun handleFollowingNotification(notification: FollowingNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(false)
                root.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.isClickable = true
            }
        }

        private fun handleActivityMessageNotification(notification: ActivityMessageNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(notification.message != null)
                notificationViewDetail.clicks {
                    notification.message?.let {
                        listener.showDetail(notification.message.message)
                    }
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToActivity(MessageActivity(notification.activityId))
                }
                root.isClickable = true
            }
        }

        private fun handleActivityMentionNotification(notification: ActivityMentionNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(notification.activity != null)
                notificationViewDetail.clicks {
                    notification.activity?.let {
                        listener.showDetail(notification.activity.message(appSetting))
                    }
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToActivity(notification.activity)
                }
                root.isClickable = true
            }
        }

        private fun handleActivityReplyNotification(notification: ActivityReplyNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(notification.activity != null)
                notificationViewDetail.clicks {
                    notification.activity?.let {
                        listener.showDetail(notification.activity.message(appSetting))
                    }
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToActivity(notification.activity)
                }
                root.isClickable = true
            }
        }

        private fun handleActivityReplySubscribedNotification(notification: ActivityReplySubscribedNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(notification.activity != null)
                notificationViewDetail.clicks {
                    notification.activity?.let {
                        listener.showDetail(notification.activity.message(appSetting))
                    }
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToActivity(notification.activity)
                }
                root.isClickable = true
            }
        }

        private fun handleActivityLikeNotification(notification: ActivityLikeNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(notification.activity != null)
                notificationViewDetail.clicks {
                    notification.activity?.let {
                        listener.showDetail(notification.activity.message(appSetting))
                    }
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToActivity(notification.activity)
                }
                root.isClickable = true
            }
        }

        private fun handleActivityReplyLikeNotification(notification: ActivityReplyLikeNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(notification.activity != null)
                notificationViewDetail.clicks {
                    notification.activity?.let {
                        listener.showDetail(notification.activity.message(appSetting))
                    }
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToActivity(notification.activity)
                }
                root.isClickable = true
            }
        }

        private fun handleThreadCommentMentionNotification(notification: ThreadCommentMentionNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(true)
                notificationViewDetail.clicks {
                    listener.showDetail(notification.comment.comment)
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToThreadComment(notification.comment)
                }
                root.isClickable = true
            }
        }

        private fun handleThreadCommentReplyNotification(notification: ThreadCommentReplyNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(true)
                notificationViewDetail.clicks {
                    listener.showDetail(notification.comment.comment)
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToThreadComment(notification.comment)
                }
                root.isClickable = true
            }
        }

        private fun handleThreadCommentSubscribedNotification(notification: ThreadCommentSubscribedNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(true)
                notificationViewDetail.clicks {
                    listener.showDetail(notification.comment.comment)
                }
                notificationViewDetail.clicks {
                    listener.showDetail(notification.comment.comment)
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToThreadComment(notification.comment)
                }
                root.isClickable = true
            }
        }

        private fun handleThreadCommentLikeNotification(notification: ThreadCommentLikeNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(true)
                notificationViewDetail.clicks {
                    listener.showDetail(notification.comment.comment)
                }
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToThreadComment(notification.comment)
                }
                root.isClickable = true
            }
        }

        private fun handleThreadLikeNotification(notification: ThreadLikeNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.user.avatar.getImageUrl(appSetting), notificationImage)
                notificationViewDetail.makeVisible(false)
                notificationImage.clicks {
                    listener.navigateToUser(notification.user)
                }
                root.clicks {
                    listener.navigateToThread(notification.thread)
                }
                root.isClickable = true
            }
        }

        private fun handleRelatedMediaAdditionNotification(notification: RelatedMediaAdditionNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.media.getCoverImage(appSetting), notificationImage)
                notificationViewDetail.makeVisible(false)
                root.clicks {
                    listener.navigateToMedia(notification.media)
                }
                root.isClickable = true
            }
        }

        private fun handleMediaDataChangeNotification(notification: MediaDataChangeNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.media.getCoverImage(appSetting), notificationImage)
                notificationViewDetail.makeVisible(true)
                notificationViewDetail.clicks {
                    listener.showDetail(notification.reason)
                }
                root.clicks {
                    listener.navigateToMedia(notification.media)
                }
                root.isClickable = true
            }
        }

        private fun handleMediaMergeNotification(notification: MediaMergeNotification) {
            binding.apply {
                ImageUtil.loadImage(context, notification.media.getCoverImage(appSetting), notificationImage)
                notificationViewDetail.makeVisible(true)
                notificationViewDetail.clicks {
                    listener.showDetail(notification.reason)
                }
                root.clicks {
                    listener.navigateToMedia(notification.media)
                }
                root.isClickable = true
            }
        }

        private fun handleMediaDeletionNotification(notification: MediaDeletionNotification) {
            binding.apply {
                ImageUtil.loadImage(context, 0, notificationImage)
                notificationViewDetail.makeVisible(true)
                notificationViewDetail.clicks {
                    listener.showDetail(notification.reason)
                }
                root.isClickable = false
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: Notification?, index: Int) {
            // do nothing
        }
    }


    interface NotificationsListener {
        fun navigateToUser(user: User)
        fun navigateToMedia(media: Media)
        fun navigateToActivity(activity: Activity?)
        fun navigateToThreadComment(threadComment: ThreadComment)
        fun navigateToThread(thread: Thread)
        fun showDetail(text: String)
    }
}