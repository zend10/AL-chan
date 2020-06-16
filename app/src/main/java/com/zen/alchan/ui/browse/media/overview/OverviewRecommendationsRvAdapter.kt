package com.zen.alchan.ui.browse.media.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.MediaRecommendations
import kotlinx.android.synthetic.main.list_media_recommendations.view.*
import type.MediaType

class OverviewRecommendationsRvAdapter(private val context: Context,
                                       private val list: List<MediaRecommendations>,
                                       private val itemWidth: Int,
                                       private val listener: OverviewRecommendationsListener
) : RecyclerView.Adapter<OverviewRecommendationsRvAdapter.ViewHolder>() {

    interface OverviewRecommendationsListener {
        fun passSelectedRecommendations(mediaId: Int, mediaType: MediaType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_recommendations, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.coverImage).into(holder.mediaRecCoverImage)
        holder.mediaRecTitleText.text = item.title
        holder.mediaRecFormatText.text = item.format?.name?.replace("_", " ")
        holder.mediaRecRatingText.text = "${context.getString(R.string.recommendation_rating)} ${item.rating ?: 0}"
        holder.mediaRatingText.text = item.averageScore?.toString() ?: "0"
        holder.mediaFavText.text = item.favourites?.toString() ?: "0"

        holder.itemView.setOnClickListener { listener.passSelectedRecommendations(item.mediaId, item.type ?: MediaType.ANIME) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaRecCoverImage = view.mediaRecCoverImage!!
        val mediaRecTitleText = view.mediaRecTitleText!!
        val mediaRecFormatText = view.mediaRecFormatText!!
        val mediaRecRatingText = view.mediaRecRatingText!!
        val mediaRatingText = view.mediaRatingText!!
        val mediaFavText = view.mediaFavText!!
    }
}