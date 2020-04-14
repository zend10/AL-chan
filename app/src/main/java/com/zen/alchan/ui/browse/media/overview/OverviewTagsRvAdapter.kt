package com.zen.alchan.ui.browse.media.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.MediaTags
import kotlinx.android.synthetic.main.list_media_tags.view.*

class OverviewTagsRvAdapter(private val context: Context,
                            private val list: List<MediaTags>,
                            private val listener: OverviewTagsListener
) : RecyclerView.Adapter<OverviewTagsRvAdapter.ViewHolder>() {

    interface OverviewTagsListener {
        fun passSelectedTag(tagId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_tags, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tagNameText.text = item.name
        holder.tagRankText.text = "${item.rank ?: "0"}%"
        holder.itemView.setOnClickListener { listener.passSelectedTag(item.id) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tagNameText = view.tagNameText!!
        val tagRankText = view.tagRankText!!
    }
}