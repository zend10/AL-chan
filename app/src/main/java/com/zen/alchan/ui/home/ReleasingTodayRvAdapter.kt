package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.ui.animelist.list.AnimeListListener
import kotlinx.android.synthetic.main.list_anime_list_grid.view.*
import kotlinx.android.synthetic.main.list_character_media.view.*
import kotlin.math.abs

class ReleasingTodayRvAdapter(private val context: Context,
                              private val list: List<HomeFragment.ReleasingTodayItem>,
                              private val listener: ReleasingTodayListener
): RecyclerView.Adapter<ReleasingTodayRvAdapter.ViewHolder>() {

    interface ReleasingTodayListener {
        fun openBrowsePage(id: Int)
        fun openEditor(mediaListId: Int)
        fun openProgressDialog(mediaList: ReleasingTodayQuery.MediaListEntry, episodeTotal: Int?)
        fun incrementProgress(mediaList: ReleasingTodayQuery.MediaListEntry, episodeTotal: Int?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_anime_list_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.releasingToday?.coverImage?.large).into(holder.animeCoverImage)
        holder.animeTitleText.text = item.releasingToday?.title?.userPreferred

        holder.animeAiringLayout.visibility = View.GONE
        holder.animePriorityIndicator.visibility = View.GONE
        holder.animeNotesLayout.visibility = View.GONE
        holder.animeScoreLayout.visibility = View.GONE

        if (item.timestamp >= 0) {
            if (item.timestamp >= 3600) {
                holder.animeFormatText.text = context.getString(R.string.ep_in_h, item.releasingToday?.nextAiringEpisode?.episode, item.timestamp / 3600)
            } else {
                holder.animeFormatText.text = context.getString(R.string.ep_in_m, item.releasingToday?.nextAiringEpisode?.episode, item.timestamp / 60)
            }
        } else if (item.timestamp < 0) {
            if (abs(item.timestamp) < 3600) {
                holder.animeFormatText.text = context.getString(R.string.ep_m_ago, item.releasingToday?.nextAiringEpisode?.episode?.minus(1), abs(item.timestamp / 60))
            } else {
                holder.animeFormatText.text = context.getString(R.string.ep_h_ago, item.releasingToday?.nextAiringEpisode?.episode?.minus(1), abs(item.timestamp / 3600))
            }
        }

        holder.animeProgressText.text = "${item.releasingToday?.mediaListEntry?.progress}/${item.releasingToday?.episodes ?: '?'}"

        holder.animeProgressLayout.setOnClickListener {
            listener.openProgressDialog(item.releasingToday?.mediaListEntry!!, item.releasingToday.episodes)
        }

        holder.animeIncrementProgressLayout.visibility = if (item.releasingToday?.episodes != null && item.releasingToday.episodes > (item.releasingToday.mediaListEntry?.progress ?: 0)) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.animeIncrementProgressLayout.setOnClickListener {
            listener.incrementProgress(item.releasingToday?.mediaListEntry!!, item.releasingToday.episodes)
        }

        holder.animeCoverImage.setOnClickListener {
            listener.openEditor(item.releasingToday?.mediaListEntry?.id!!)
        }

        holder.animeTitleLayout.setOnClickListener {
            listener.openBrowsePage(item.releasingToday?.id!!)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animeTitleLayout = view.animeTitleLayout!!
        val animeTitleText = view.animeTitleText!!
        val animeCoverImage = view.animeCoverImage!!
        val animeFormatText = view.animeFormatText!!
        val animeScoreLayout = view.animeScoreLayout!!
        val animeProgressText = view.animeProgressText!!
        val animeProgressLayout = view.animeProgressLayout!!
        val animeAiringLayout = view.animeAiringLayout!!
        val animePriorityIndicator = view.animePriorityIndicator!!
        val animeNotesLayout = view.animeNotesLayout!!
        val animeIncrementProgressLayout = view.animeIncrementProgressLayout!!
    }
}