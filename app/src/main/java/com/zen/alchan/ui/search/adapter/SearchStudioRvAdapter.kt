package com.zen.alchan.ui.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.SearchResult
import com.zen.alchan.ui.search.SearchListener
import kotlinx.android.synthetic.main.list_search.view.*

class SearchStudioRvAdapter(private val context: Context,
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
            val item = list[position]
            holder.searchNameText.text = item?.studiosSearchResult?.name
            holder.searchFavoriteText.text = item?.studiosSearchResult?.favourites?.toString() ?: "0"

            if (item?.studiosSearchResult?.media?.edges?.isNullOrEmpty() == false) {
                GlideApp.with(context).load(item.studiosSearchResult?.media?.edges!![0]?.node?.coverImage?.large).into(holder.searchImage)
            }

            holder.searchInfoLayout.visibility = View.GONE
            holder.searchScoreText.visibility = View.GONE

            holder.itemView.setOnClickListener {
                listener.passSelectedItem(item?.studiosSearchResult?.id!!)
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
        val searchInfoLayout = view.searchInfoLayout!!
        val searchScoreText = view.searchScoreText!!
        val searchFavoriteText = view.searchFavoriteText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}