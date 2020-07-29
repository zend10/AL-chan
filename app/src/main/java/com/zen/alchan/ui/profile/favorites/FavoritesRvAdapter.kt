package com.zen.alchan.ui.profile.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.FavoriteItem
import kotlinx.android.synthetic.main.list_media_cover_grid.view.*
import kotlinx.android.synthetic.main.list_subtitle.view.*

class FavoritesRvAdapter(private val context: Context,
                         private val list: List<FavoriteItem>,
                         private val listener: FavoritesListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_TITLE = 0
        const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_cover_grid, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_subtitle, parent, false)
            TitleViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]
            GlideApp.with(context).load(item.image).into(holder.mediaCoverImage)
            holder.itemView.setOnClickListener { listener.passSelectedItem(item.id!!, item.browsePage) }
        } else if (holder is TitleViewHolder) {
            val item = list[position]
            holder.subtitleText.text = when (item.browsePage) {
                BrowsePage.ANIME -> context.getString(R.string.anime)
                BrowsePage.MANGA -> context.getString(R.string.manga)
                BrowsePage.CHARACTER -> context.getString(R.string.characters)
                BrowsePage.STAFF -> context.getString(R.string.staffs)
                else -> ""
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].id == null) VIEW_TYPE_TITLE else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mediaCoverImage = view.mediaCoverImage!!
    }

    class TitleViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val subtitleText = view.subtitleText!!
    }
}