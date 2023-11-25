package com.zen.alchan.ui.media.mediasocial

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.ListActivityMediaBinding
import com.zen.alchan.databinding.ListFollowingMediaListBinding
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.databinding.ListTitleBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.MediaSocialItem
import com.zen.alchan.helper.pojo.SocialItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.type.ScoreFormat
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class MediaSocialRvAdapter(
    private val context: Context,
    list: List<MediaSocialItem?>,
    private val viewer: User?,
    private val appSetting: AppSetting,
    private val listener: MediaSocialListener
) : BaseRecyclerViewAdapter<MediaSocialItem?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MediaSocialItem.VIEW_TYPE_FOLLOWING_MEDIA_LIST -> {
                val view = ListFollowingMediaListBinding.inflate(inflater, parent, false)
                FollowingMediaListViewHolder(view)
            }
            MediaSocialItem.VIEW_TYPE_FOLLOWING_MEDIA_LIST_HEADER -> {
                val view = ListTitleBinding.inflate(inflater, parent, false)
                FollowingMediaListHeaderViewHolder(view)
            }
            MediaSocialItem.VIEW_TYPE_FOLLOWING_MEDIA_LIST_SEE_MORE -> {
                val view = ListTextBinding.inflate(inflater, parent, false)
                FollowingMediaListSeeMoreViewHolder(view)
            }
            MediaSocialItem.VIEW_TYPE_MEDIA_ACTIVITY -> {
                val view = ListActivityMediaBinding.inflate(inflater, parent, false)
                MediaActivityViewHolder(view)
            }
            MediaSocialItem.VIEW_TYPE_MEDIA_ACTIVITY_HEADER -> {
                val view = ListTitleBinding.inflate(inflater, parent, false)
                MediaActivityHeaderViewHolder(view)
            }
            MediaSocialItem.VIEW_TYPE_MEDIA_ACTIVITY_SEE_MORE -> {
                val view = ListTextBinding.inflate(inflater, parent, false)
                MediaActivitySeeMoreViewHolder(view)
            }
            else -> {
                val view = ListActivityMediaBinding.inflate(inflater, parent, false)
                MediaActivityViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position]?.viewType ?: VIEW_TYPE_LOADING
    }


    inner class FollowingMediaListViewHolder(private val binding: ListFollowingMediaListBinding) : ViewHolder(binding) {
        override fun bind(item: MediaSocialItem?, index: Int) {
            if (item?.mediaList == null)
                return

            with(binding) {
                ImageUtil.loadCircleImage(context, item.mediaList.user.avatar.getImageUrl(appSetting), mediaListFollowingUserAvatar)
                mediaListFollowingUserName.text = item.mediaList.user.name

                val statusStringBuilder = StringBuilder()
                val mediaType = if (item.mediaList.media.type == com.zen.alchan.type.MediaType.MANGA) MediaType.MANGA else MediaType.ANIME
                statusStringBuilder.append(item.mediaList.status?.getString(mediaType))
                if (mediaType == MediaType.ANIME && item.mediaList.progress != 0) {
                    val episodes = if (item.mediaList.media.episodes == 0) "" else "/${item.mediaList.media.episodes}"
                    statusStringBuilder.append(" ep. ${item.mediaList.progress}${episodes}")
                } else if (mediaType == MediaType.MANGA && item.mediaList.progress != 0) {
                    val chapters = if (item.mediaList.media.chapters == 0) "" else "/${item.mediaList.media.chapters}"
                    statusStringBuilder.append(" ch. ${item.mediaList.progress}${chapters}")
                } else if (mediaType == MediaType.MANGA && item.mediaList.progressVolumes != 0) {
                    val volumes = if (item.mediaList.media.volumes == 0) "" else "/${item.mediaList.media.volumes}"
                    statusStringBuilder.append(" vo. ${item.mediaList.progressVolumes}${volumes}")
                } else {
                    statusStringBuilder.append("")
                }
                mediaListFollowingStatus.text = statusStringBuilder

                if (item.mediaList.user.mediaListOptions.scoreFormat == ScoreFormat.POINT_3) {
                    mediaListFollowingScoreText.show(false)
                    ImageUtil.loadImage(context, item.mediaList.getScoreSmiley(), mediaListFollowingScoreIcon)
                } else {
                    mediaListFollowingScoreText.show(true)
                    mediaListFollowingScoreText.text = item.mediaList.getScore()
                    ImageUtil.loadImage(context, R.drawable.ic_star_filled, mediaListFollowingScoreIcon)
                }

                mediaListFollowingUserAvatar.clicks {
                    listener.navigateToUser(item.mediaList.user)
                }
            }
        }
    }

    inner class FollowingMediaListHeaderViewHolder(private val binding: ListTitleBinding) : ViewHolder(binding) {
        override fun bind(item: MediaSocialItem?, index: Int) {
            binding.titleText.text = context.getString(R.string.friends_list)
        }
    }

    inner class FollowingMediaListSeeMoreViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: MediaSocialItem?, index: Int) {
            with(binding) {
                itemText.text = context.getString(R.string.see_more)
                itemText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                itemText.setTextAppearance(context.getAttrValue(R.attr.themeRegularClickableBoldFont))
                upperItemDivider.root.show(false)
                lowerItemDivider.root.show(false)
                root.clicks {
                    listener.seeMoreFollowingMediaList()
                }
            }
        }
    }

    inner class MediaActivityViewHolder(private val binding: ListActivityMediaBinding) : ViewHolder(binding) {
        override fun bind(item: MediaSocialItem?, index: Int) {
            if (item?.activity == null)
                return

            with(binding) {
                activityMediaUserName.text = item.activity.user().name
                activityMediaDateTime.text = TimeUtil.displayInDayDateTimeFormat(item.activity.createdAt)
                activityMediaStatus.text = "${item.activity.status} ${item.activity.progress}".replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
                ImageUtil.loadCircleImage(context, item.activity.user().avatar.getImageUrl(appSetting), activityMediaUserAvatar)
                activityMediaUserAvatar.clicks {
                    listener.navigateToUser(item.activity.user())
                }
            }
        }
    }

    inner class MediaActivityHeaderViewHolder(private val binding: ListTitleBinding) : ViewHolder(binding) {
        override fun bind(item: MediaSocialItem?, index: Int) {
            binding.titleText.text = context.getString(R.string.recent_activies)
            binding.titleText.updatePadding(top = if (index == 0) 0 else context.resources.getDimensionPixelSize(R.dimen.marginPageBig))
        }
    }

    inner class MediaActivitySeeMoreViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: MediaSocialItem?, index: Int) {
            with(binding) {
                itemText.text = context.getString(R.string.see_more)
                itemText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                itemText.setTextAppearance(context.getAttrValue(R.attr.themeRegularClickableBoldFont))
                upperItemDivider.root.show(false)
                lowerItemDivider.root.show(false)
                root.clicks {
                    listener.seeMoreMediaActivity()
                }
            }
        }
    }

    interface MediaSocialListener {
        fun navigateToUser(user: User)
        fun seeMoreFollowingMediaList()
        fun seeMoreMediaActivity()
    }
}