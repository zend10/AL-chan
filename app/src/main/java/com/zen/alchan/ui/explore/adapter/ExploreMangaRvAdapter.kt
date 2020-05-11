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

class ExploreMangaRvAdapter(private val context: Context,
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
            val item = list[position]?.mangaSearchResult
            GlideApp.with(context).load(item?.coverImage?.large).into(holder.exploreCoverImage)
            holder.exploreTitleText.text = item?.title?.userPreferred
            holder.exploreYearText.text = item?.startDate?.year?.toString() ?: "TBA"
            holder.exploreFormatText.text = item?.format?.name?.replaceUnderscore() ?: "TBA"
            holder.exploreScoreText.text = item?.averageScore?.toString() ?: "0"
            holder.exploreFavoriteText.text = item?.favourites?.toString() ?: "0"

            if (item?.staff != null && item.staff.edges?.isNullOrEmpty() == false) {
                var creatorList = ""
                item.staff.edges.forEachIndexed { index, it ->
                    creatorList += it?.node?.name?.full
                    if (index != item.staff.edges.lastIndex) creatorList += ", "
                }
                holder.exploreCreatorText.text = creatorList
            } else {
                holder.exploreCreatorText.text = "TBA"
            }

            if (item?.chapters != null && item.chapters != 0) {
                holder.exploreCountIcon.visibility = View.VISIBLE
                holder.exploreCountText.visibility = View.VISIBLE
                holder.exploreCountText.text = "${item.chapters} ${"chapter".setRegularPlural(item.chapters)}"
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
                holder.exploreGenreRecyclerView.visibility = View.GONE
            }

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
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}