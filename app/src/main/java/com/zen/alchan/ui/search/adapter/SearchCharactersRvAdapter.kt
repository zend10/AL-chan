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

class SearchCharactersRvAdapter(private val context: Context,
                                private val list: List<SearchResult?>,
                                private val showRank: Boolean,
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
            holder.searchNameText.text = item?.charactersSearchResult?.name?.full
            GlideApp.with(context).load(item?.charactersSearchResult?.image?.large).into(holder.searchImage)
            holder.searchFavoriteText.text = item?.charactersSearchResult?.favourites?.toString() ?: "0"

            holder.searchInfoLayout.visibility = View.GONE
            holder.searchScoreText.visibility = View.GONE

            holder.itemView.setOnClickListener {
                listener.passSelectedItem(item?.charactersSearchResult?.id!!)
            }

            if (showRank) {
                holder.entryRankLayout.visibility = View.VISIBLE
                holder.entryRankText.text = (position + 1).toString()
            } else {
                holder.entryRankLayout.visibility = View.GONE
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
        val entryRankLayout = view.entryRankLayout!!
        val entryRankText = view.entryRankText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}