package com.zen.alchan.ui.browse.media.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import kotlinx.android.synthetic.main.list_media_stats_ranking.view.*

class MediaStatsRankingRvAdapter(private val list: List<MediaStatsQuery.Ranking?>) : RecyclerView.Adapter<MediaStatsRankingRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_stats_ranking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.mediaStatsRankingText.text = "#${item?.rank} ${item?.context}${if (item?.season != null) " " + item.season else ""}${if (item?.year != null) " " + item.year else ""}"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mediaStatsRankingText = view.mediaStatsRankingText!!
    }
}