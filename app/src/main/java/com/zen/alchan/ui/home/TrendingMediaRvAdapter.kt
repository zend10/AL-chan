package com.zen.alchan.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_trending_media.view.*
import type.MediaType

class TrendingMediaRvAdapter(private val context: Context,
                             private val list: List<HomeFragment.TrendingMediaItem>,
                             private val mediaType: MediaType,
                             private val itemWidth: Int,
                             private val listener: TrendingMediaListener
) : RecyclerView.Adapter<TrendingMediaRvAdapter.ViewHolder>() {

    interface TrendingMediaListener {
        fun passSelectedMedia(position: Int, mediaType: MediaType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_trending_media, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.trendingMedia?.coverImage?.large).into(holder.trendingMediaImage)
        if (item.isSelected) {
            holder.trendingMediaCard.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeSecondaryColor))
        } else {
            holder.trendingMediaCard.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }
        holder.itemView.setOnClickListener { listener.passSelectedMedia(position, mediaType) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val trendingMediaCard = view.trendingMediaCard!!
        val trendingMediaImage = view.trendingMediaImage!!
    }
}