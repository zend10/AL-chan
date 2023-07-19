package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.LayoutHomeHeaderBinding
import com.zen.alchan.databinding.LayoutHomeMenuBinding
import com.zen.alchan.databinding.LayoutHomeReleasingTodayBinding
import com.zen.alchan.databinding.LayoutHomeSocialBinding
import com.zen.alchan.databinding.LayoutHomeTrendingBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.HomeItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class HomeRvAdapter(
    private val context: Context,
    list: List<HomeItem>,
    private val user: User?,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: HomeListener
) : BaseRecyclerViewAdapter<HomeItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            HomeItem.VIEW_TYPE_HEADER -> {
                val view = LayoutHomeHeaderBinding.inflate(inflater, parent, false)
                return HeaderViewHolder(view)
            }
            HomeItem.VIEW_TYPE_MENU -> {
                val view = LayoutHomeMenuBinding.inflate(inflater, parent, false)
                return MenuViewHolder(view)
            }
            HomeItem.VIEW_TYPE_RELEASING_TODAY -> {
                val view = LayoutHomeReleasingTodayBinding.inflate(inflater, parent, false)
                return ReleasingTodayViewHolder(view)
            }
            HomeItem.VIEW_TYPE_SOCIAL -> {
                val view = LayoutHomeSocialBinding.inflate(inflater, parent, false)
                return SocialViewHolder(view)
            }
            HomeItem.VIEW_TYPE_TRENDING_ANIME, HomeItem.VIEW_TYPE_TRENDING_MANGA -> {
                val view = LayoutHomeTrendingBinding.inflate(inflater, parent, false)
                return TrendingMediaViewHolder(view)
            }
            else -> {
                val view = LayoutHomeHeaderBinding.inflate(inflater, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class HeaderViewHolder(private val binding: LayoutHomeHeaderBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            binding.apply {
                if (user?.bannerImage?.isNotBlank() == true)
                    ImageUtil.loadImage(context, user.bannerImage, headerImage)

                if (user?.name?.isNotBlank() == true)
                    welcomeText.text = context.getString(R.string.hello_user, user.name)
                else
                    welcomeText.text = context.getString(R.string.hello)

                user?.let {
                    homeHeaderAvatar.show(true)
                    if (appSetting.useCircularAvatarForProfile)
                        ImageUtil.loadCircleImage(context, it.avatar.getImageUrl(appSetting), homeHeaderAvatar)
                    else
                        ImageUtil.loadRectangleImage(context, it.avatar.getImageUrl(appSetting), homeHeaderAvatar)
                } ?: homeHeaderAvatar.show(false)

                searchLayout.clicks { listener.headerListener.navigateToSearch() }
            }
        }
    }

    inner class MenuViewHolder(private val binding: LayoutHomeMenuBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            binding.apply {
                seasonalMenu.clicks { listener.menuListener.navigateToSeasonal() }
                exploreMenu.clicks { listener.menuListener.showExploreDialog() }
                reviewsMenu.clicks { listener.menuListener.navigateToReview() }
                calendarMenu.clicks { listener.menuListener.navigateToCalendar() }
            }
        }
    }

    inner class ReleasingTodayViewHolder(private val binding: LayoutHomeReleasingTodayBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            with(binding) {
                if (item.releasingToday.isNotEmpty()) {
                    releasingTodayRecyclerView.adapter = ReleasingTodayRvAdapter(context, item.releasingToday, appSetting, listener.releasingTodayListener)
                    releasingTodayRecyclerView.show(true)
                    releasingTodayEmptyText.show(false)
                } else {
                    releasingTodayRecyclerView.show(false)
                    releasingTodayEmptyText.show(true)
                }
            }
        }
    }

    inner class SocialViewHolder(private val binding: LayoutHomeSocialBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            binding.apply {
                homeSocialJoinButton.clicks { listener.socialListener.navigateToSocial() }
                root.clicks { listener.socialListener.navigateToSocial() }
            }
        }
    }

    inner class TrendingMediaViewHolder(private val binding: LayoutHomeTrendingBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            binding.apply {
                trendingRightNowText.text = when (item.viewType) {
                    HomeItem.VIEW_TYPE_TRENDING_ANIME -> context.getString(R.string.trending_anime_right_now)
                    HomeItem.VIEW_TYPE_TRENDING_MANGA -> context.getString(R.string.trending_manga_right_now)
                    else -> ""
                }

                if (item.media.isNotEmpty()) {
                    trendingListRecyclerView.adapter = TrendingMediaRvAdapter(context, item.media, appSetting, width, listener.trendingMediaListener)
                    trendingProgressBar.show(false)
                } else {
                    trendingProgressBar.show(true)
                }
            }
        }
    }
}