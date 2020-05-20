package com.zen.alchan.ui.profile.stats.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import kotlinx.android.synthetic.main.list_media_cover_grid.view.*
import type.MediaType

class StatsDetailMediaRvAdapter(private val context: Context,
                                private val list: List<StatsDetailRvAdapter.StatsDetailMediaItem>,
                                private val listener: StatsDetailMediaListener
): RecyclerView.Adapter<StatsDetailMediaRvAdapter.ViewHolder>() {

    interface StatsDetailMediaListener {
        fun passSelectedMedia(id: Int, mediaType: MediaType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_cover_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.image).into(holder.mediaCoverImage)
        holder.itemView.setOnClickListener { listener.passSelectedMedia(item.id, item.mediaType) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaCoverImage = view.mediaCoverImage!!
    }
}