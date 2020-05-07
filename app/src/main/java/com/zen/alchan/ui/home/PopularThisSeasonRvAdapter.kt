package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import kotlinx.android.synthetic.main.list_media_relations.view.*

class PopularThisSeasonRvAdapter(private val context: Context,
                                 private val list: List<PopularSeasonQuery.Medium>,
                                 private val itemWidth: Int,
                                 private val listener: PopularThisSeasonListener
) : RecyclerView.Adapter<PopularThisSeasonRvAdapter.ViewHolder>() {

    interface PopularThisSeasonListener {
        fun passSelectedAnime(mediaId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_relations, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.coverImage()?.large()).into(holder.mediaImage)
        holder.mediaFormatLayout.visibility = View.GONE
        holder.mediaTitleText.text = item.title()?.userPreferred()
        if (item.studios()?.edges()?.isNullOrEmpty() == false) {
            holder.mediaRelationText.text = item.studios()?.edges()!![0].node()?.name()
        } else {
            holder.mediaRelationText.text = ""
        }
        holder.itemView.setOnClickListener { listener.passSelectedAnime(item.id()) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaImage = view.mediaImage!!
        val mediaFormatLayout = view.mediaFormatLayout!!
        val mediaTitleText = view.mediaTitleText!!
        val mediaRelationText = view.mediaRelationText!!
    }
}