package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.setRegularPlural
import kotlinx.android.synthetic.main.list_character_media.view.*
import kotlin.math.abs

class ReleasingTodayRvAdapter(private val context: Context,
                              private val list: List<HomeFragment.ReleasingTodayItem>,
                              private val listener: ReleasingTodayListener
): RecyclerView.Adapter<ReleasingTodayRvAdapter.ViewHolder>() {

    interface ReleasingTodayListener {
        fun passSelectedAnime(mediaId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_character_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.releasingToday.coverImage()?.large()).into(holder.mediaCoverImage)
        holder.mediaTitleText.text = item.releasingToday.title()?.userPreferred()
        holder.characterRoleLayout.visibility = View.GONE
        if (item.timestamp >= 0) {
            if (item.timestamp >= 3600) {
                holder.airingTimeText.text = "Airing in ${item.timestamp / 3600} ${"hour".setRegularPlural(item.timestamp / 3600)}"
            } else {
                holder.airingTimeText.text = "Airing in ${item.timestamp / 60} ${"minute".setRegularPlural(item.timestamp / 60)}"
            }
        } else if (item.timestamp < 0) {
            if (abs(item.timestamp) < 3600) {
                holder.airingTimeText.text = "Airing ${abs(item.timestamp / 60)} ${"minute".setRegularPlural(item.timestamp / 60)} ago"
            } else {
                holder.airingTimeText.text = "Airing ${abs(item.timestamp / 3600)} ${"hour".setRegularPlural(item.timestamp / 3600)} ago"
            }
        }

        holder.mediaCoverImage.setOnClickListener {
            listener.passSelectedAnime(item.releasingToday.id())
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaCoverImage = view.mediaCoverImage!!
        val mediaTitleText = view.mediaTitleText!!
        val airingTimeText = view.mediaFormatText!!
        val characterRoleLayout = view.characterRoleLayout!!
    }
}