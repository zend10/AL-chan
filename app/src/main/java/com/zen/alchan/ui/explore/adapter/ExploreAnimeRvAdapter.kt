package com.zen.alchan.ui.explore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.SearchResult
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.ui.browse.media.overview.OverviewGenreRvAdapter
import com.zen.alchan.ui.search.SearchListener
import kotlinx.android.synthetic.main.list_explore_anime.view.*
import type.MediaListStatus

class ExploreAnimeRvAdapter(private val context: Context,
                            private val list: List<SearchResult?>,
                            private val listener: SearchListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_explore_anime, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]?.animeSearchResult
            GlideApp.with(context).load(item?.coverImage?.large).into(holder.exploreCoverImage)
            holder.exploreTitleText.text = item?.title?.userPreferred
            holder.exploreYearText.text = item?.startDate?.year?.toString() ?: "TBA"
            holder.exploreFormatText.text = item?.format?.name?.replaceUnderscore() ?: "TBA"
            holder.exploreScoreText.text = item?.averageScore?.toString() ?: "0"
            holder.exploreFavoriteText.text = item?.favourites?.toString() ?: "0"

            if (item?.studios != null && item.studios.edges?.isNullOrEmpty() == false) {
                holder.exploreCreatorText.text = item.studios.edges[0]?.node?.name
            } else {
                holder.exploreCreatorText.text = "TBA"
            }

            if (item?.episodes != null && item.episodes != 0) {
                holder.exploreCountIcon.visibility = View.VISIBLE
                holder.exploreCountText.visibility = View.VISIBLE
                holder.exploreCountText.text = "${item.episodes} ${"episode".setRegularPlural(item.episodes)}"
            } else {
                holder.exploreCountIcon.visibility = View.GONE
                holder.exploreCountText.visibility = View.GONE
            }

            if (!item?.genres.isNullOrEmpty()) {
                holder.exploreGenreRecyclerView.visibility = View.VISIBLE
                holder.exploreGenreRecyclerView.adapter = OverviewGenreRvAdapter(item?.genres!!, object : OverviewGenreRvAdapter.OverviewGenreListener {
                    override fun passSelectedGenre(genre: String) { }
                })
            } else {
                holder.exploreGenreRecyclerView.visibility = View.INVISIBLE
            }

            if (item?.mediaListEntry != null) {
                holder.userStatusLayout.visibility = View.VISIBLE
                holder.userStatusText.text = if (item.mediaListEntry.status == MediaListStatus.CURRENT) context.getString(R.string.watching_caps) else item.mediaListEntry.status?.name.replaceUnderscore()
            } else {
                holder.userStatusLayout.visibility = View.GONE
            }

            holder.entryRankText.text = (position + 1).toString()

            holder.itemView.setOnClickListener {
                listener.passSelectedItem(item?.id!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exploreCoverImage = view.exploreCoverImage!!
        val exploreTitleText = view.exploreTitleText!!
        val exploreCreatorText = view.exploreCreatorText!!
        val exploreYearText = view.exploreYearText!!
        val exploreFormatText = view.exploreFormatText!!
        val exploreCountIcon = view.exploreCountIcon!!
        val exploreCountText = view.exploreCountText!!
        val exploreScoreText = view.exploreScoreText!!
        val exploreFavoriteText = view.exploreFavoriteText!!
        val exploreGenreRecyclerView = view.exploreGenreRecyclerView!!
        val userStatusLayout = view.userStatusLayout!!
        val userStatusText = view.userStatusText!!
        val entryRankText = view.entryRankText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}