package com.zen.alchan.ui.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.setRegularPlural
import kotlinx.android.synthetic.main.list_calendar_schedule.view.*
import type.MediaListStatus
import java.text.SimpleDateFormat
import java.util.*

class CalendarScheduleRvAdapter(private val context: Context,
                                private val list: List<AiringScheduleQuery.AiringSchedule>,
                                val listener: CalendarScheduleListener
) : RecyclerView.Adapter<CalendarScheduleRvAdapter.ViewHolder>() {

    interface CalendarScheduleListener {
        fun openMedia(id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_calendar_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        holder.scheduleTimeText.text = timeFormat.format(item.airingAt * 1000L)
        holder.scheduleTitleText.text = item.media?.title?.userPreferred

        GlideApp.with(context).load(item.media?.bannerImage).into(holder.bannerImage)

        holder.scheduleEpisodeText.text = when {
            item.timeUntilAiring >= 3600 -> {
                context.getString(R.string.ep_in, item.episode, item.timeUntilAiring / 3600, context.getString(R.string.hour).setRegularPlural(item.timeUntilAiring / 3600))
            }
            item.timeUntilAiring <= 0 -> {
                context.getString(R.string.ep, item.episode)
            }
            else -> {
                context.getString(R.string.ep_in, item.episode, item.timeUntilAiring / 60, context.getString(R.string.minute).setRegularPlural(item.timeUntilAiring / 60))
            }
        }

        if (item.media?.mediaListEntry != null) {
            holder.userStatusText.visibility = View.VISIBLE
            holder.userStatusIcon.visibility = View.VISIBLE

            holder.userStatusText.text = if (item.media.mediaListEntry.status == MediaListStatus.CURRENT) context.getString(R.string.watching_caps) else item.media.mediaListEntry.status?.name.replaceUnderscore()

            val statusColor = Constant.STATUS_COLOR_MAP[item.media.mediaListEntry.status] ?: Constant.STATUS_COLOR_LIST[0]
            holder.userStatusText.setTextColor(statusColor)
            holder.userStatusIcon.setColorFilter(statusColor)
        } else {
            holder.userStatusText.visibility = View.INVISIBLE
            holder.userStatusIcon.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener { listener.openMedia(item.media?.id ?: 0) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scheduleTimeText = view.scheduleTimeText!!
        val scheduleTitleText = view.scheduleTitleText!!
        val scheduleEpisodeText = view.scheduleEpisodeText!!
        val userStatusIcon = view.userStatusIcon!!
        val userStatusText = view.userStatusText!!
        val bannerImage = view.bannerImage!!
    }
}