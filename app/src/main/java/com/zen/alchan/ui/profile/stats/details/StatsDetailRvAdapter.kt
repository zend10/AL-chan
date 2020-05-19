package com.zen.alchan.ui.profile.stats.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.StatsCategory
import com.zen.alchan.helper.pojo.UserStatsData
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.roundToTwoDecimal
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_stats_detail.view.*
import type.MediaType

class StatsDetailRvAdapter(private val context: Context,
                           private val list: List<UserStatsData>,
                           private val listener: StatsDetailListener
): RecyclerView.Adapter<StatsDetailRvAdapter.ViewHolder>() {

    interface StatsDetailListener {
        fun passSelectedData(id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_stats_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (list[position].statsCategory) {
            StatsCategory.FORMAT -> handleFormatLayout(holder, position)
            StatsCategory.STATUS -> handleStatusLayout(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun handleFormatLayout(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.statsRankText.visibility = View.GONE
        holder.statsMeanScoreLayout.visibility = View.VISIBLE
        holder.statsMediaRecyclerView.visibility = View.GONE

        holder.statsLabelText.setTextColor(item.color ?: AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        holder.statsLabelText.text = item.label
        holder.statsCountText.text = item.count?.toString() ?: "0"
        holder.statsScoreText.text = item.meanScore?.roundToTwoDecimal()

        if (item.mediaType == MediaType.ANIME) {
            holder.statsProgressLabel.text = context.getString(R.string.time_watched)
            val days = ((item.minutesWatched ?: 0) / 60 / 24)
            val hours = ((item.minutesWatched ?: 0) - (days * 60 * 24)) / 60
            var progressText = ""
            if (days != 0) {
                progressText += "$days ${"day".setRegularPlural(days)}"
            }
            if (hours != 0) {
                if (days != 0) {
                    progressText += " "
                }
                progressText += "$hours ${"hour".setRegularPlural(hours)}"
            }
            holder.statsProgressText.text = progressText
        } else {
            holder.statsProgressLabel.text = context.getString(R.string.chapters_read)
            holder.statsProgressText.text = item.chaptersRead?.toString() ?: "0"
        }
    }

    private fun handleStatusLayout(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.statsRankText.visibility = View.GONE
        holder.statsMeanScoreLayout.visibility = View.VISIBLE
        holder.statsMediaRecyclerView.visibility = View.GONE

        holder.statsLabelText.setTextColor(item.color ?: AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        holder.statsLabelText.text = item.label
        holder.statsCountText.text = item.count?.toString() ?: "0"
        holder.statsScoreText.text = item.meanScore?.roundToTwoDecimal()

        if (item.mediaType == MediaType.ANIME) {
            holder.statsProgressLabel.text = context.getString(R.string.time_watched)
            val days = ((item.minutesWatched ?: 0) / 60 / 24)
            val hours = ((item.minutesWatched ?: 0) - (days * 60 * 24)) / 60
            var progressText = ""
            if (days != 0) {
                progressText += "$days ${"day".setRegularPlural(days)}"
            }
            if (hours != 0) {
                if (days != 0) {
                    progressText += " "
                }
                progressText += "$hours ${"hour".setRegularPlural(hours)}"
            }
            holder.statsProgressText.text = progressText
        } else {
            holder.statsProgressLabel.text = context.getString(R.string.chapters_read)
            holder.statsProgressText.text = item.chaptersRead?.toString() ?: "0"
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val statsRankText = view.statsRankText!!
        val statsLabelText = view.statsLabelText!!
        val statsCountText = view.statsCountText!!
        val statsProgressLabel = view.statsProgressLabel!!
        val statsProgressText = view.statsProgressText!!
        val statsMeanScoreLayout = view.statsMeanScoreLayout!!
        val statsScoreText = view.statsScoreText!!
        val statsMediaRecyclerView = view.statsMediaRecyclerView!!
    }
}