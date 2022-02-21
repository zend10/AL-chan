package com.zen.alchan.ui.userstats

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.zen.alchan.R
import com.zen.alchan.data.response.anilist.UserLengthStatistic
import com.zen.alchan.data.response.anilist.UserStaffStatistic
import com.zen.alchan.data.response.anilist.UserStudioStatistic
import com.zen.alchan.data.response.anilist.UserVoiceActorStatistic
import com.zen.alchan.databinding.ListStatsChartBarBinding
import com.zen.alchan.databinding.ListStatsChartLineBinding
import com.zen.alchan.databinding.ListStatsChartPieBinding
import com.zen.alchan.databinding.ListStatsInfoBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.UserStatsItem
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlin.math.round

class UserStatsRvAdapter(
    private val context: Context,
    list: List<UserStatsItem>,
    private val listener: UserStatsListener
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
                    if (item.isClickable)
                        context.getAttrValue(R.attr.themePrimaryColor)
                    else if (item.color.isNullOrBlank())
                        context.getAttrValue(R.attr.themeSecondaryColor)
                    else
                        Color.parseColor(item.color)
                )

                statsInfoRankLayout.show(item.showRank)
                statsInfoRankText.text = (index + 1).formatTwoDigit()

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

                if (item.isClickable) {
                    root.clicks {
                        when (item.stats) {
                            is UserVoiceActorStatistic -> listener.navigateToStaff(item.stats.voiceActor?.id ?: 0)
                            is UserStaffStatistic -> listener.navigateToStaff(item.stats.staff?.id ?: 0)
                            is UserStudioStatistic -> listener.navigateToStudio(item.stats.studio?.id ?: 0)
                            else -> Unit
                        }
                    }
                    root.isClickable = true
                } else {
                    root.clicks { Unit }
                    root.isClickable = false
                }
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

            if (stringBuilder.isBlank()) {
                stringBuilder.append("1 ${context.resources.getQuantityString(R.plurals.hour, 1)}")
            }

            return stringBuilder.toString()
        }
    }

    inner class BarChartViewHolder(private val binding: ListStatsChartBarBinding) : ViewHolder(binding) {
        override fun bind(item: UserStatsItem, index: Int) {
            binding.apply {
                val useStringLabel = item.chart?.any{ it.label.toDoubleOrNull() == null } ?: false

                val newValueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }

                val barEntries = item.chart?.mapIndexed { index, chart ->
                    BarEntry(if (useStringLabel) index * 10F + 10F else (chart.label.toFloatOrNull() ?: index * 10F + 10F), chart.value.toFloat())
                }
                val barDataSet = BarDataSet(barEntries, "")
                barDataSet.colors = item.chart?.map { if (it.color.isNullOrBlank()) context.getAttrValue(R.attr.themeSecondaryColor) else Color.parseColor(it.color) }

                val barData = BarData(barDataSet)
                barData.setValueTextColor(context.getAttrValue(R.attr.themeContentColor))
                barData.barWidth = 3F
                barData.setValueFormatter(newValueFormatter)

                statsBarChart.axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setDrawLabels(false)
                }

                statsBarChart.axisRight.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setDrawLabels(false)
                }

                statsBarChart.xAxis.apply {
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    setLabelCount(barDataSet.entryCount, true)
                    textColor = context.getAttrValue(R.attr.themeContentColor)

                    if (!item.chart.isNullOrEmpty() && useStringLabel) {
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val labelIndex = round(value / 10.0) - 1
                                if (labelIndex >= 0 && labelIndex < item.chart.size) {
                                    return item.chart[labelIndex.toInt()].label
                                }
                                return ""
                            }
                        }
                    }
                }

                statsBarChart.apply {
                    setTouchEnabled(false)
                    description.isEnabled = false
                    legend.isEnabled = false
                    data = barData
                    invalidate()
                }
            }
        }
    }

    inner class LineChartViewHolder(private val binding: ListStatsChartLineBinding) : ViewHolder(binding) {
        override fun bind(item: UserStatsItem, index: Int) {
            binding.apply {
                val newValueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }

                val lineEntries = item.chart?.mapIndexed { index, chart -> Entry(chart.label.toFloat(), chart.value.toFloat()) }
                val lineDataSet = LineDataSet(lineEntries, "")
                lineDataSet.color = context.getAttrValue(R.attr.themeSecondaryColor)
                lineDataSet.setDrawFilled(true)
                lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.drawable.background_line_chart)

                val lineData = LineData(lineDataSet)
                lineData.setValueTextColor(context.getAttrValue(R.attr.themeContentColor))
                lineData.setValueFormatter(newValueFormatter)

                statsLineChart.axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setDrawLabels(false)
                }

                statsLineChart.axisRight.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setDrawLabels(false)
                }

                statsLineChart.xAxis.apply {
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1F
                    textColor = context.getAttrValue(R.attr.themeContentColor)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    }
                }

                statsLineChart.apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    data = lineData
                    invalidate()
                }
            }
        }
    }

    inner class PieChartViewHolder(private val binding: ListStatsChartPieBinding) : ViewHolder(binding) {
        override fun bind(item: UserStatsItem, index: Int) {
            binding.apply {
                val pieEntries = item.chart?.mapIndexed { index, chart -> PieEntry(chart.value.toFloat(), chart.label) }
                val pieDataSet = PieDataSet(pieEntries, "")
                pieDataSet.colors = item.chart?.map { if (it.color.isNullOrBlank()) context.getAttrValue(R.attr.themeSecondaryColor) else Color.parseColor(it.color) }

                val pieData = PieData(pieDataSet)
                pieData.setDrawValues(false)

                statsPieChart.apply {
                    setHoleColor(ContextCompat.getColor(context, android.R.color.transparent))
                    setDrawEntryLabels(false)
                    setTouchEnabled(false)
                    description.isEnabled = false
                    legend.isEnabled = false
                    data = pieData
                    invalidate()
                }
            }
        }
    }

    interface UserStatsListener {
        fun navigateToStaff(id: Int)
        fun navigateToStudio(id: Int)
    }
}