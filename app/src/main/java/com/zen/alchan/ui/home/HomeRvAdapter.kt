package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
) : BaseRecyclerViewAdapter<HomeItem>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(listener.headerListener)
            is MenuViewHolder -> holder.bind(listener.menuListener)
            is TrendingMediaViewHolder -> holder.bind(context, list[position].media, list[position].viewType, width, listener.trendingMediaListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    class HeaderViewHolder(private val binding: LayoutHomeHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: HomeListener.HeaderListener) {
            binding.searchLayout.clicks { listener.navigateToSearch() }
        }
    }

    class MenuViewHolder(private val binding: LayoutHomeMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: HomeListener.MenuListener) {
            binding.apply {
                seasonalMenu.clicks { listener.navigateToSeasonal() }
                exploreMenu.clicks { listener.showExploreDialog() }
                reviewsMenu.clicks { listener.navigateToReviews() }
                calendarMenu.clicks { listener.navigateToCalendar() }
            }
        }
    }

    class TrendingMediaViewHolder(private val binding: LayoutHomeTrendingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, trendingMedia: List<Media>, viewType: Int, width: Int, listener: HomeListener.TrendingMediaListener) {
            binding.apply {
                trendingRightNowText.text = when (viewType) {
                    HomeItem.VIEW_TYPE_TRENDING_ANIME -> context.getString(R.string.trending_anime_right_now)
                    HomeItem.VIEW_TYPE_TRENDING_MANGA -> context.getString(R.string.trending_manga_right_now)
                    else -> ""
                }

                if (trendingMedia.isNotEmpty()) {
                    trendingListRecyclerView.adapter = TrendingMediaRvAdapter(context, trendingMedia, width, listener)
                    trendingProgressBar.visibility = View.GONE
                } else {
                    trendingProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }
}