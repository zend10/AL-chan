package com.zen.alchan.ui.profile.stats.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.StatsCategory
import com.zen.alchan.helper.pojo.UserStatsData
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.roundToTwoDecimal
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_stats_detail.view.*
import type.MediaType
import type.UserStatisticsSort
import kotlin.math.roundToInt

class StatsDetailRvAdapter(private val context: Context,
                           private val list: List<UserStatsData>,
                           private val statsCategory: StatsCategory,
                           private val mediaType: MediaType,
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
        when (statsCategory) {
            StatsCategory.FORMAT -> handleFormatLayout(holder, position)
            StatsCategory.STATUS -> handleStatusLayout(holder, position)
            StatsCategory.SCORE -> handleScoreLayout(holder, position)
            StatsCategory.LENGTH -> handleLengthLayout(holder, position)
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
        holder.statsCountPercentageText.text = statsCountPercentageText(item)
        holder.statsProgressLabel.text = getProgressLabel()
        holder.statsProgressText.text = getProgressString(item)
        holder.statsProgressPercentageText.text= getProgressPercentageString(item)
        holder.statsScoreText.text = item.meanScore?.roundToTwoDecimal()
    }

    private fun handleStatusLayout(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.statsRankText.visibility = View.GONE
        holder.statsMeanScoreLayout.visibility = View.VISIBLE
        holder.statsMediaRecyclerView.visibility = View.GONE

        holder.statsLabelText.setTextColor(item.color ?: AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        holder.statsLabelText.text = item.label
        holder.statsCountText.text = item.count?.toString() ?: "0"
        holder.statsCountPercentageText.text = statsCountPercentageText(item)
        holder.statsProgressLabel.text = getProgressLabel()
        holder.statsProgressText.text = getProgressString(item)
        holder.statsProgressPercentageText.text= getProgressPercentageString(item)
        holder.statsScoreText.text = item.meanScore?.roundToTwoDecimal()
    }

    private fun handleScoreLayout(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.statsRankText.visibility = View.GONE
        holder.statsMeanScoreLayout.visibility = View.GONE
        holder.statsMediaRecyclerView.visibility = View.GONE

        holder.statsLabelText.setTextColor(item.color ?: AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        holder.statsLabelText.text = item.label
        holder.statsCountText.text = item.count?.toString() ?: "0"
        holder.statsCountPercentageText.text = statsCountPercentageText(item)
        holder.statsProgressLabel.text = getProgressLabel()
        holder.statsProgressText.text = getProgressString(item)
        holder.statsProgressPercentageText.text= getProgressPercentageString(item)
    }

    private fun handleLengthLayout(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.statsRankText.visibility = View.GONE
        holder.statsMeanScoreLayout.visibility = View.VISIBLE
        holder.statsMediaRecyclerView.visibility = View.GONE

        holder.statsLabelText.setTextColor(item.color ?: AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        holder.statsLabelText.text = item.label
        holder.statsCountText.text = item.count?.toString() ?: "0"
        holder.statsCountPercentageText.text = statsCountPercentageText(item)
        holder.statsProgressLabel.text = getProgressLabel()
        holder.statsProgressText.text = getProgressString(item)
        holder.statsProgressPercentageText.text= getProgressPercentageString(item)
        holder.statsScoreText.text = item.meanScore?.roundToTwoDecimal()
    }

    private fun statsCountPercentageText(item: UserStatsData): String {
        if (item.count == null) {
            return "(0%)"
        }

        val percentage = (item.count / list.sumByDouble { it.count?.toDouble()!! } * 100).roundToTwoDecimal()

        return "($percentage%)"
    }

    private fun getProgressLabel(): String {
        return if (mediaType == MediaType.ANIME) {
            context.getString(R.string.time_watched)
        } else {
            context.getString(R.string.chapters_read)
        }
    }

    private fun getProgressString(item: UserStatsData): String {
        if (mediaType == MediaType.ANIME) {
            if (item.minutesWatched == null || item.minutesWatched == 0) {
                return "0 hours"
            }
            val days = (item.minutesWatched / 60 / 24)
            val hours = (item.minutesWatched - (days * 60 * 24)) / 60
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
            return progressText
        } else {
            return item.chaptersRead?.toString() ?: "0"
        }
    }

    private fun getProgressPercentageString(item: UserStatsData): String {
        if (mediaType == MediaType.ANIME) {
            if (item.minutesWatched == null || item.minutesWatched == 0) {
                return "(0%)"
            }
            val percentage = (item.minutesWatched / list.sumByDouble { it.minutesWatched?.toDouble()!! } * 100).roundToTwoDecimal()
            return "($percentage%)"
        } else {
            if (item.chaptersRead == null || item.chaptersRead == 0) {
                return "(0%)"
            }
            val percentage = (item.chaptersRead / list.sumByDouble { it.chaptersRead?.toDouble()!! } * 100).roundToTwoDecimal()
            return "($percentage%)"
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val statsRankText = view.statsRankText!!
        val statsLabelText = view.statsLabelText!!
        val statsCountText = view.statsCountText!!
        val statsCountPercentageText = view.statsCountPercentageText!!
        val statsProgressLabel = view.statsProgressLabel!!
        val statsProgressText = view.statsProgressText!!
        val statsProgressPercentageText = view.statsProgressPercentageText!!
        val statsMeanScoreLayout = view.statsMeanScoreLayout!!
        val statsScoreText = view.statsScoreText!!
        val statsMediaRecyclerView = view.statsMediaRecyclerView!!
    }
}