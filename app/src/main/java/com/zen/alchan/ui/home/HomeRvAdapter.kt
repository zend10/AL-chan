package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.response.Media
import com.zen.alchan.helper.pojo.HomeItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.layout_home_header.view.*
import kotlinx.android.synthetic.main.layout_home_menu.view.*
import kotlinx.android.synthetic.main.layout_home_trending.view.*
import type.MediaType
import java.util.*

class HomeRvAdapter(
    private val context: Context,
    list: List<HomeItem>,
    private val width: Int,
    private val listener: HomeListener
) : BaseRecyclerViewAdapter<HomeItem>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            HomeItem.VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_home_header, parent, false)
                return HeaderViewHolder(view)
            }
            HomeItem.VIEW_TYPE_MENU -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_home_menu, parent, false)
                return MenuViewHolder(view)
            }
            HomeItem.VIEW_TYPE_TRENDING_ANIME, HomeItem.VIEW_TYPE_TRENDING_MANGA -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_home_trending, parent, false)
                return TrendingMediaViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_home_header, parent, false)
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

    class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(listener: HomeListener.HeaderListener) {
            view.searchLayout.setOnClickListener { listener.navigateToSearch() }
        }
    }

    class MenuViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(listener: HomeListener.MenuListener) {
            view.seasonalMenu.setOnClickListener { listener.navigateToSeasonal() }
            view.exploreMenu.setOnClickListener { listener.showExploreDialog() }
            view.reviewsMenu.setOnClickListener { listener.navigateToReviews() }
            view.calendarMenu.setOnClickListener { listener.navigateToCalendar() }
        }
    }

    class TrendingMediaViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(context: Context, trendingMedia: List<Media>, viewType: Int, width: Int, listener: HomeListener.TrendingMediaListener) {
            view.trendingRightNowText.text = when (viewType) {
                HomeItem.VIEW_TYPE_TRENDING_ANIME -> context.getString(R.string.trending_anime_right_now)
                HomeItem.VIEW_TYPE_TRENDING_MANGA -> context.getString(R.string.trending_manga_right_now)
                else -> ""
            }

            if (trendingMedia.isNotEmpty()) {
                view.trendingListRecyclerView.adapter = TrendingMediaRvAdapter(context, trendingMedia, width, listener)
                view.trendingProgressBar.visibility = View.GONE
            } else {
                view.trendingProgressBar.visibility = View.VISIBLE
            }
        }
    }
}