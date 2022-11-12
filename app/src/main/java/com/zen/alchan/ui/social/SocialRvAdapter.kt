package com.zen.alchan.ui.social

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.ListActivityBinding
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.SocialItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import type.MediaStatus

class SocialRvAdapter(
    private val context: Context,
    list: List<SocialItem>,
    private val viewer: User?,
    private val appSetting: AppSetting,
    private val viewDetails: Boolean,
    private val listener: SocialListener
) : BaseRecyclerViewAdapter<SocialItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SocialItem.VIEW_TYPE_FRIENDS_ACTIVITY_HEADER -> {
                val view = ListTextBinding.inflate(inflater, parent, false)
                FriendsActivityHeaderViewHolder(view)
            }
            SocialItem.VIEW_TYPE_FRIENDS_ACTIVITY_SEE_MORE -> {
                val view = ListTextBinding.inflate(inflater, parent, false)
                FriendsActivitySeeMoreViewHolder(view)
            }
            SocialItem.VIEW_TYPE_GLOBAL_ACTIVITY_HEADER -> {
                val view = ListTextBinding.inflate(inflater, parent, false)
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
            else -> {
                val view = ListActivityBinding.inflate(inflater, parent, false)
                ActivityViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class ActivityViewHolder(private val binding: ListActivityBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem, index: Int) {
            with(binding) {
                if (item.activity == null)
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

                activityTime.text = TimeUtil.displayInDayDateTimeFormat(activity.createdAt)

                MarkdownUtil.applyMarkdown(context, activityText, activity.message(appSetting))

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
                    listener.navigateToActivityDetail(activity)
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

                root.clicks {
                    listener.navigateToActivityDetail(activity)
                }
            }
        }
    }

    inner class FriendsActivityHeaderViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem, index: Int) {
            binding.itemText.text = context.getString(R.string.friends_recent_activities)
        }
    }

    inner class FriendsActivitySeeMoreViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem, index: Int) {
            with(binding) {
                itemText.text = context.getString(R.string.see_more)
                itemText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                itemText.setTextAppearance(context.getAttrValue(R.attr.themeRegularClickableBoldFont))
                upperItemDivider.root.show(false)
                lowerItemDivider.root.show(true)
                root.clicks {
                    listener.navigateToActivityList()
                }
            }
        }
    }

    inner class GlobalActivityHeaderViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem, index: Int) {
            binding.itemText.text = context.getString(R.string.global_activity)
        }
    }

    inner class GlobalActivitySeeMoreViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: SocialItem, index: Int) {
            with(binding) {
                itemText.text = context.getString(R.string.see_more)
                itemText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                itemText.setTextAppearance(context.getAttrValue(R.attr.themeRegularClickableBoldFont))
                upperItemDivider.root.show(false)
                lowerItemDivider.root.show(true)
                root.clicks {
                    listener.navigateToActivityList()
                }
            }
        }
    }
}