package com.zen.alchan.ui.browse.media.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.MediaRelations
import kotlinx.android.synthetic.main.list_media_relations.view.*
import type.MediaType

class OverviewRelationsRvAdapter(private val context: Context,
                                 private val list: List<MediaRelations>,
                                 private val itemWidth: Int,
                                 private val listener: OverviewRelationsListener
) : RecyclerView.Adapter<OverviewRelationsRvAdapter.ViewHolder>() {

    interface OverviewRelationsListener {
        fun passSelectedRelations(mediaId: Int, mediaType: MediaType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_relations, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.coverImage).into(holder.mediaImage)
        holder.mediaFormatText.text = item.format.name.replace("_", " ")
        holder.mediaTitleText.text = item.title
        holder.mediaRelationText.text = item.relationType.name.replace("_", " ")
        holder.itemView.setOnClickListener { listener.passSelectedRelations(item.mediaId, item.mediaType) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaImage = view.mediaImage!!
        val mediaFormatText = view.mediaFormatText!!
        val mediaTitleText = view.mediaTitleText!!
        val mediaRelationText = view.mediaRelationText!!
    }
}