package com.zen.alchan.ui.social

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.User
import com.zen.databinding.*
import com.zen.alchan.helper.enums.ActivityListPage
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.SocialItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.MarkdownSetup
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.type.MediaStatus

class SocialRvAdapter(
    private val context: Context,
    list: List<SocialItem?>,
    private val width: Int,
    private val viewer: User?,
    private val appSetting: AppSetting,
    private val viewDetails: Boolean,
    private val listener: SocialListener
) : BaseRecyclerViewAdapter<SocialItem?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(view)
            }
            SocialItem.VIEW_TYPE_FRIENDS_ACTIVITY_HEADER -> {
                val view = ListTitleBinding.inflate(inflater, parent, false)
                FriendsActivityHeaderViewHolder(view)
            }
            SocialItem.VIEW_TYPE_FRIENDS_ACTIVITY_SEE_MORE -> {
                val view = ListTextBinding.inflate(inflater, parent, false)
                FriendsActivitySeeMoreViewHolder(view)
            }
            SocialItem.VIEW_TYPE_GLOBAL_ACTIVITY_HEADER -> {
                val view = ListTitleBinding.inflate(inflater, parent, false)
                GlobalActivityHeaderViewHolder(view)
            }
            SocialItem.VIEW_TYPE_GLOBAL_ACTIVITY_SEE_MORE -> {
                val view = ListTextBinding.inflate(inflater, parent, false)
                GlobalActivitySeeMoreViewHolder(view)
            }
            SocialItem.VIEW_TYPE_ACTIVITY -> {
                val view = ListActivityBinding.inflate(inflater, parent, false)
                ActivityViewHolder(view)
            }
            SocialItem.VIEW_TYPE_ACTIVITY_REPLY -> {
                val view = ListActivityReplyBinding.inflate(inflater, parent, false)
                ActivityReplyViewHolder(view)
            }
            else -> {
                val view = ListActivityBinding.inflate(inflater, parent, false)
                ActivityViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position]?.viewType ?: VIEW_TYPE_LOADING
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem?, index: Int) {
            // do nothing
        }
    }

    inner class ActivityViewHolder(private val binding: ListActivityBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem?, index: Int) {
            with(binding) {
                if (item?.activity == null)
                    return

                val activity = item.activity

                ImageUtil.loadCircleImage(context, activity.user().avatar.getImageUrl(appSetting), activityUserAvatar)
                activityUserAvatar.clicks {
                    listener.navigateToUser(activity.user())
                }
                activityUserName.text = activity.user().name
                activityUserName.clicks {
                    listener.navigateToUser(activity.user())
                }

                activityRecipientArrowIcon.show(activity.hasRecipient())
                activityRecipientName.show(activity.hasRecipient())
                activityRecipientName.text = activity.recipient().name
                activityRecipientName.clicks {
                    listener.navigateToUser(activity.recipient())
                }

                activityPrivateBadge.badgeCard.show(activity.isPrivateMessage())
                activityPrivateBadge.badgeText.text = context.getString(R.string.private_)

                activityTime.text = TimeUtil.displayInDayDateTimeFormat(activity.createdAt)

                MarkdownUtil.applyMarkdown(context, width, activityText, activity.message(appSetting))

                activityMediaPreviewLayout.show(activity.hasMedia())
                activityMediaPreviewTitle.text = activity.media().getTitle(appSetting)
                activityMediaPreviewYear.text = activity.media().startDate?.year?.toString() ?: "TBA"
                activityMediaPreviewYear.show(activity.media().startDate?.year != null || activity.media().status == MediaStatus.NOT_YET_RELEASED)
                activityMediaPreviewFormat.text = activity.media().getFormattedMediaFormat(true)
                ImageUtil.loadImage(context, activity.media().getCoverImage(appSetting), activityMediaPreviewImage)
                activityMediaPreviewLayout.clicks {
                    listener.navigateToMedia(activity.media())
                }

                activityReplyText.show(activity.replyCount != 0)
                activityReplyText.text = activity.replyCount.getNumberFormatting()
                activityReplyLayout.clicks {
                    listener.reply(activity)
                }

                activityLikeText.show(activity.likeCount != 0)
                activityLikeText.text = activity.likeCount.getNumberFormatting()
                if (activity.isLiked) {
                    activityLikeText.setTextColor(context.getThemePrimaryColor())
                    activityLikeIcon.imageTintList = ColorStateList.valueOf(context.getThemePrimaryColor())
                } else {
                    activityLikeText.setTextColor(context.getThemeTextColor())
                    activityLikeIcon.imageTintList = ColorStateList.valueOf(context.getThemeTextColor())
                }
                activityLikeAvatar.show(activity.likeCount > 0)
                activityLikeAvatar.circleCount = if (activity.likeCount >= 3) 3 else activity.likeCount
                ImageUtil.loadImagesIntoOverlapImageListView(context, activity.likes.take(3).map { it.avatar.medium }, activityLikeAvatar)
                activityLikeAvatar.clicks {
                    listener.viewLikes(activity)
                }
                activityLikeLayout.clicks {
                    listener.toggleLike(activity)
                }

                activitySubscribeIcon.imageTintList = ColorStateList.valueOf(
                    if (activity.isSubscribed) context.getThemePrimaryColor() else context.getThemeTextColor()
                )
                activitySubscribeLayout.clicks {
                    listener.toggleSubscribe(activity)
                }

                activityMoreLayout.clicks {
                    val popupWrapper = ContextThemeWrapper(context, R.style.PopupTheme)
                    val popupMenu = PopupMenu(popupWrapper, activityMoreIcon)
                    popupMenu.menuInflater.inflate(R.menu.menu_activity, popupMenu.menu)
                    popupMenu.menu.findItem(R.id.itemEdit).isVisible = activity.isEditable(viewer)
                    popupMenu.menu.findItem(R.id.itemDelete).isVisible = activity.isDeletable(viewer)
                    popupMenu.menu.findItem(R.id.itemReport).isVisible = activity.isReportable(viewer)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.itemEdit -> listener.edit(activity)
                            R.id.itemDelete -> listener.delete(activity)
                            R.id.itemViewOnAniList -> listener.viewOnAniList(activity)
                            R.id.itemCopyLink -> listener.copyActivityLink(activity)
                            R.id.itemReport -> listener.report(activity)
                        }
                        true
                    }
                    popupMenu.show()
                }

                root.isEnabled = !viewDetails
                root.clicks {
                    listener.navigateToActivityDetail(activity)
                }
            }
        }
    }

    inner class ActivityReplyViewHolder(private val binding: ListActivityReplyBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem?, index: Int) {
            with(binding) {
                if (item?.activity == null || item.activityReply == null)
                    return

                val activity = item.activity
                val activityReply = item.activityReply

                ImageUtil.loadCircleImage(context, activityReply.user.avatar.getImageUrl(appSetting), activityUserAvatar)
                activityUserAvatar.clicks {
                    listener.navigateToUser(activityReply.user)
                }
                activityUserName.text = activityReply.user.name
                activityUserName.clicks {
                    listener.navigateToUser(activityReply.user)
                }

                activityTime.text = TimeUtil.displayInDayDateTimeFormat(activityReply.createdAt)

                MarkdownUtil.applyMarkdown(context, width, activityText, activityReply.text)

                activityReplyLayout.clicks {
                    listener.reply(activity, activityReply)
                }

                activityLikeText.show(activityReply.likeCount != 0)
                activityLikeText.text = activityReply.likeCount.getNumberFormatting()
                if (activityReply.isLiked) {
                    activityLikeText.setTextColor(context.getThemePrimaryColor())
                    activityLikeIcon.imageTintList = ColorStateList.valueOf(context.getThemePrimaryColor())
                } else {
                    activityLikeText.setTextColor(context.getThemeTextColor())
                    activityLikeIcon.imageTintList = ColorStateList.valueOf(context.getThemeTextColor())
                }
                activityLikeAvatar.show(activityReply.likeCount > 0)
                activityLikeAvatar.circleCount = if (activityReply.likeCount >= 3) 3 else activityReply.likeCount
                ImageUtil.loadImagesIntoOverlapImageListView(context, activityReply.likes.take(3).map { it.avatar.medium }, activityLikeAvatar)
                activityLikeAvatar.clicks {
                    listener.viewLikes(activity, activityReply)
                }
                activityLikeLayout.clicks {
                    listener.toggleLike(activity, activityReply)
                }

                activityMoreLayout.clicks {
                    val popupWrapper = ContextThemeWrapper(context, R.style.PopupTheme)
                    val popupMenu = PopupMenu(popupWrapper, activityMoreIcon)
                    popupMenu.menuInflater.inflate(R.menu.menu_activity, popupMenu.menu)
                    popupMenu.menu.findItem(R.id.itemEdit).isVisible = viewer?.id == activityReply.user.id
                    popupMenu.menu.findItem(R.id.itemDelete).isVisible = viewer?.id == activityReply.user.id
                    popupMenu.menu.findItem(R.id.itemReport).isVisible = viewer?.id != activityReply.user.id
                    popupMenu.menu.findItem(R.id.itemViewOnAniList).isVisible = false
                    popupMenu.menu.findItem(R.id.itemCopyLink).isVisible = false
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.itemEdit -> listener.edit(activity, activityReply)
                            R.id.itemDelete -> listener.delete(activity, activityReply)
                            R.id.itemReport -> listener.report(activity)
                        }
                        true
                    }
                    popupMenu.show()
                }

                root.isEnabled = false
            }
        }
    }

    inner class FriendsActivityHeaderViewHolder(private val binding: ListTitleBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem?, index: Int) {
            binding.titleText.text = context.getString(R.string.friends_recent_activities)
        }
    }

    inner class FriendsActivitySeeMoreViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem?, index: Int) {
            with(binding) {
                itemText.text = context.getString(R.string.see_more)
                itemText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                itemText.setTextAppearance(context.getAttrValue(R.attr.themeRegularClickableBoldFont))
                upperItemDivider.root.show(false)
                lowerItemDivider.root.show(false)
                root.clicks {
                    listener.navigateToActivityList(ActivityListPage.FRIENDS)
                }
            }
        }
    }

    inner class GlobalActivityHeaderViewHolder(private val binding: ListTitleBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem?, index: Int) {
            binding.titleText.text = context.getString(R.string.global_activity)
            binding.titleText.updatePadding(top = context.resources.getDimensionPixelSize(R.dimen.marginPageBig))
        }
    }

    inner class GlobalActivitySeeMoreViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem?, index: Int) {
            with(binding) {
                itemText.text = context.getString(R.string.see_more)
                itemText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                itemText.setTextAppearance(context.getAttrValue(R.attr.themeRegularClickableBoldFont))
                upperItemDivider.root.show(false)
                lowerItemDivider.root.show(false)
                root.clicks {
                    listener.navigateToActivityList(ActivityListPage.GLOBAL)
                }
            }
        }
    }
}