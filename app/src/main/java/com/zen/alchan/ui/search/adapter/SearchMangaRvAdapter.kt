package com.zen.alchan.ui.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.SearchResult
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.ui.search.SearchListener
import kotlinx.android.synthetic.main.list_search.view.*
import type.MediaListStatus

class SearchMangaRvAdapter(private val context: Context,
                           private val list: List<SearchResult?>,
                           private val listener: SearchListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_search, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]?.mangaSearchResult
            holder.searchNameText.text = item?.title?.userPreferred
            holder.searchYearText.text = item?.startDate?.year?.toString() ?: "TBA"
            holder.searchFormatText.text = item?.format?.name?.replaceUnderscore()
            GlideApp.with(context).load(item?.coverImage?.large).into(holder.searchImage)
            holder.searchScoreText.text = item?.averageScore?.toString() ?: "0"
            holder.searchFavoriteText.text = item?.favourites?.toString() ?: "0"
            holder.itemView.setOnClickListener {
                listener.passSelectedItem(item?.id!!)
            }

            if (item?.mediaListEntry?.status != null) {
                holder.userStatusLayout.visibility = View.VISIBLE

                holder.userStatusText.text = if (item.mediaListEntry.status == MediaListStatus.CURRENT) context.getString(R.string.reading_caps) else item.mediaListEntry.status.name.replaceUnderscore()

                val statusColor = Constant.STATUS_COLOR_MAP[item.mediaListEntry.status] ?: Constant.STATUS_COLOR_LIST[0]
                holder.userStatusText.setTextColor(statusColor)
                holder.userStatusIcon.setColorFilter(statusColor)
            } else {
                holder.userStatusLayout.visibility = View.GONE
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
        val searchImage = view.searchImage!!
        val searchNameText = view.searchNameText!!
        val searchYearText = view.searchYearText!!
        val searchFormatText = view.searchFormatText!!
        val searchScoreText = view.searchScoreText!!
        val searchFavoriteText = view.searchFavoriteText!!
        val userStatusLayout = view.userStatusLayout!!
        val userStatusIcon = view.userStatusIcon!!
        val userStatusText = view.userStatusText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}