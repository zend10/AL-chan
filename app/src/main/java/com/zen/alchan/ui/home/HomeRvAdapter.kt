package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.LayoutHomeHeaderBinding
import com.zen.alchan.databinding.LayoutHomeMenuBinding
import com.zen.alchan.databinding.LayoutHomeTrendingBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.HomeItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class HomeRvAdapter(
    private val context: Context,
    list: List<HomeItem>,
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
            binding.searchLayout.clicks { listener.headerListener.navigateToSearch() }
        }
    }

    inner class MenuViewHolder(private val binding: LayoutHomeMenuBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            binding.apply {
                seasonalMenu.clicks { listener.menuListener.navigateToSeasonal() }
                exploreMenu.clicks { listener.menuListener.showExploreDialog() }
                reviewsMenu.clicks { listener.menuListener.navigateToReviews() }
                calendarMenu.clicks { listener.menuListener.navigateToCalendar() }
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
                    trendingListRecyclerView.adapter = TrendingMediaRvAdapter(context, item.media, width, listener.trendingMediaListener)
                    trendingProgressBar.visibility = View.GONE
                } else {
                    trendingProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }
}