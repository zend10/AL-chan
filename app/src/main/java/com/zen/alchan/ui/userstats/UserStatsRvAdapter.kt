package com.zen.alchan.ui.userstats

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.databinding.ListStatsChartBarBinding
import com.zen.alchan.databinding.ListStatsChartLineBinding
import com.zen.alchan.databinding.ListStatsChartPieBinding
import com.zen.alchan.databinding.ListStatsInfoBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.formatTwoDecimal
import com.zen.alchan.helper.extensions.getAttrValue
import com.zen.alchan.helper.extensions.getNumberFormatting
import com.zen.alchan.helper.extensions.roundToTwoDecimal
import com.zen.alchan.helper.pojo.UserStatsItem
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class UserStatsRvAdapter(
    private val context: Context,
    list: List<UserStatsItem>
) : BaseRecyclerViewAdapter<UserStatsItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            UserStatsItem.VIEW_TYPE_PIE_CHART -> {
                val view = ListStatsChartPieBinding.inflate(inflater, parent, false)
                return PieChartViewHolder(view)
            }
            UserStatsItem.VIEW_TYPE_LINE_CHART -> {
                val view = ListStatsChartLineBinding.inflate(inflater, parent, false)
                return LineChartViewHolder(view)
            }
            UserStatsItem.VIEW_TYPE_BAR_CHART -> {
                val view = ListStatsChartBarBinding.inflate(inflater, parent, false)
                return BarChartViewHolder(view)
            }
            UserStatsItem.VIEW_TYPE_INFO -> {
                val view = ListStatsInfoBinding.inflate(inflater, parent, false)
                return InfoViewHolder(view)
            }
            else -> {
                val view = ListStatsInfoBinding.inflate(inflater, parent, false)
                return InfoViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class InfoViewHolder(private val binding: ListStatsInfoBinding) : ViewHolder(binding) {
        override fun bind(item: UserStatsItem, index: Int) {
            binding.apply {
                statsInfoItemLabel.text = item.label
                statsInfoItemLabel.setTextColor(
                    if (item.color.isNullOrBlank())
                        context.getAttrValue(R.attr.themeSecondaryColor)
                    else
                        Color.parseColor(item.color)
                )

                statsInfoItemCount.text = item.stats?.count?.toString() ?: ""
                statsInfoItemCountPercentage.text = item.countPercentage

                when (item.mediaType) {
                    MediaType.ANIME -> {
                        statsInfoItemDurationTitle.text = context.getString(R.string.time_watched)
                        statsInfoItemDuration.text = getDaysAndHoursString(TimeUtil.getDaysAndHours(item.stats?.minutesWatched ?: 0))
                    }
                    MediaType.MANGA -> {
                        statsInfoItemDurationTitle.text = context.getString(R.string.chapters_read)
                        statsInfoItemDuration.text = item.stats?.chaptersRead?.getNumberFormatting() ?: "0"
                    }
                }

                statsInfoItemDurationPercentage.text = item.durationPercentage

                statsInfoItemMeanScore.text = item.stats?.meanScore?.roundToTwoDecimal()
            }
        }

        private fun getDaysAndHoursString(daysAndHours: Pair<Int, Int>): String {
            val stringBuilder = StringBuilder()
            val days = daysAndHours.first
            val hours = daysAndHours.second
            if (days != 0) {
                stringBuilder.append("$days ${context.resources.getQuantityString(R.plurals.day, days)}")
            }

            if (hours != 0) {
                if (days != 0) {
                    stringBuilder.append(" ")
                }

                stringBuilder.append("$hours ${context.resources.getQuantityString(R.plurals.hour, hours)}")
            }

            return stringBuilder.toString()
        }
    }

    inner class BarChartViewHolder(private val binding: ListStatsChartBarBinding) : ViewHolder(binding) {
        override fun bind(item: UserStatsItem, index: Int) {

        }
    }

    inner class LineChartViewHolder(private val binding: ListStatsChartLineBinding) : ViewHolder(binding) {
        override fun bind(item: UserStatsItem, index: Int) {

        }
    }

    inner class PieChartViewHolder(private val binding: ListStatsChartPieBinding) : ViewHolder(binding) {
        override fun bind(item: UserStatsItem, index: Int) {

        }
    }
}