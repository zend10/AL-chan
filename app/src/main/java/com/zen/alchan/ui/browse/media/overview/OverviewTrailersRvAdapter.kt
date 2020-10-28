package com.zen.alchan.ui.browse.media.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.MediaTrailer
import kotlinx.android.synthetic.main.list_media_trailers.view.*

class OverviewTrailersRvAdapter(private val context: Context,
                                private val list: List<MediaTrailer>,
                                private val itemWidth: Int,
                                private val listener: OverviewTrailersListener
) : RecyclerView.Adapter<OverviewTrailersRvAdapter.ViewHolder>() {

    interface OverviewTrailersListener {
        fun playTrailer(site: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_trailers, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.thumbnail).into(holder.trailerThumbnail)
        holder.trailerTitle.text = item.title
        holder.itemView.setOnClickListener {
            listener.playTrailer(item.site)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trailerThumbnail = view.trailerThumbnail!!
        val trailerTitle = view.trailerTitle!!
    }
}