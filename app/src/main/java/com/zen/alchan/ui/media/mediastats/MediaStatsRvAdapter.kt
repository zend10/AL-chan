package com.zen.alchan.ui.media.mediastats

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.databinding.*
import com.zen.alchan.helper.extensions.getAttrValue
import com.zen.alchan.helper.extensions.getColor
import com.zen.alchan.helper.extensions.getNumberFormatting
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.helper.pojo.Chart
import com.zen.alchan.helper.pojo.MediaStatsItem
import com.zen.alchan.type.MediaListStatus
import com.zen.alchan.type.MediaType
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlin.math.round
import kotlin.math.roundToInt

class MediaStatsRvAdapter(
    private val context: Context,
    list: List<MediaStatsItem>
) : BaseRecyclerViewAdapter<MediaStatsItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MediaStatsItem.VIEW_TYPE_STATS_HEADER,
            MediaStatsItem.VIEW_TYPE_RANKING_HEADER,
            MediaStatsItem.VIEW_TYPE_STATUS_DISTRIBUTION_HEADER,
            MediaStatsItem.VIEW_TYPE_SCORE_DISTRIBUTION_HEADER -> {
                val view = ListTitleBinding.inflate(inflater, parent, false)
                TitleViewHolder(view)
            }
            MediaStatsItem.VIEW_TYPE_STATS -> {
                val view = LayoutMediaStatsBinding.inflate(inflater, parent, false)
                StatsViewHolder(view)
            }
            MediaStatsItem.VIEW_TYPE_RANKING -> {
                val view = ListRankingBinding.inflate(inflater, parent, false)
                RankingViewHolder(view)
            }
            MediaStatsItem.VIEW_TYPE_STATUS_DISTRIBUTION -> {
                val view = LayoutMediaStatusDistributionBinding.inflate(inflater, parent, false)
                StatusDistributionViewHolder(view)
            }
            MediaStatsItem.VIEW_TYPE_SCORE_DISTRIBUTION -> {
                val view = ListStatsChartBarBinding.inflate(inflater, parent, false)
                ScoreDistributionViewHolder(view)
            }
            else -> {
                val view = LayoutMediaStatsBinding.inflate(inflater, parent, false)
                StatsViewHolder(view)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class TitleViewHolder(private val binding: ListTitleBinding) : ViewHolder(binding) {
        override fun bind(item: MediaStatsItem, index: Int) {
            with(binding) {
                titleText.text = context.getString(
                    when (item.viewType) {
                        MediaStatsItem.VIEW_TYPE_STATS_HEADER -> R.string.performance
                        MediaStatsItem.VIEW_TYPE_RANKING_HEADER -> R.string.rankings
                        MediaStatsItem.VIEW_TYPE_STATUS_DISTRIBUTION_HEADER -> R.string.status_distribution
                        MediaStatsItem.VIEW_TYPE_SCORE_DISTRIBUTION_HEADER -> R.string.score_distribution
                        else -> 0
                    }
                )

                titleText.updatePadding(
                    top = if (item.viewType == MediaStatsItem.VIEW_TYPE_STATS_HEADER)
                        0
                    else
                        context.resources.getDimensionPixelSize(R.dimen.marginPageBig)
                )
            }
        }
    }

    inner class StatsViewHolder(private val binding: LayoutMediaStatsBinding) : ViewHolder(binding) {
        override fun bind(item: MediaStatsItem, index: Int) {
            with(binding) {
                mediaStatsAverageScore.text = item.media.averageScore.getNumberFormatting() + "%"
                mediaStatsMeanScore.text = item.media.meanScore.getNumberFormatting() + "%"
                mediaStatsPopularity.text = item.media.popularity.getNumberFormatting()
                mediaStatsFavorites.text = item.media.favourites.getNumberFormatting()
            }
        }
    }

    inner class RankingViewHolder(private val binding: ListRankingBinding) : ViewHolder(binding) {
        override fun bind(item: MediaStatsItem, index: Int) {
            with(binding) {
                val mediaRank = item.mediaRank
                rankingText.text = "#${mediaRank.rank} ${mediaRank.context}${if (mediaRank.season != null) " " + mediaRank.season else ""}${if (mediaRank.year != 0) " " + mediaRank.year else ""}"
            }
        }
    }

    inner class StatusDistributionViewHolder(private val binding: LayoutMediaStatusDistributionBinding) : ViewHolder(binding) {
        override fun bind(item: MediaStatsItem, index: Int) {
            with(binding) {
                val chart = ArrayList<Chart>()

                item.media.stats?.statusDistribution?.forEach {
                    val hexColor = it.status?.getColor()
                    val color = ColorStateList.valueOf(Color.parseColor(hexColor))
                    val label = getStatusLabel(it.status, item.media.type)
                    val amount = it.amount.getNumberFormatting()

                    chart.add(Chart(hexColor, label, it.amount.toDouble()))

                    when (it.status) {
                        MediaListStatus.CURRENT -> {
                            mediaStatsCurrentIcon.imageTintList = color
                            mediaStatsCurrentLabel.text = label
                            mediaStatsCurrentText.text = amount
                        }
                        MediaListStatus.PLANNING -> {
                            mediaStatsPlanningIcon.imageTintList = color
                            mediaStatsPlanningLabel.text = label
                            mediaStatsPlanningText.text = amount
                        }
                        MediaListStatus.COMPLETED -> {
                            mediaStatsCompletedIcon.imageTintList = color
                            mediaStatsCompletedLabel.text = label
                            mediaStatsCompletedText.text = amount
                        }
                        MediaListStatus.DROPPED -> {
                            mediaStatsDroppedIcon.imageTintList = color
                            mediaStatsDroppedLabel.text = label
                            mediaStatsDroppedText.text = amount
                        }
                        MediaListStatus.PAUSED -> {
                            mediaStatsPausedIcon.imageTintList = color
                            mediaStatsPausedLabel.text = label
                            mediaStatsPausedText.text = amount
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }

                val pieEntries = chart.mapIndexed { _, chart -> PieEntry(chart.value.toFloat(), chart.label) }
                val pieDataSet = PieDataSet(pieEntries, "")
                pieDataSet.colors = chart.map { if (it.color.isNullOrBlank()) context.getAttrValue(R.attr.themeSecondaryColor) else Color.parseColor(it.color) }

                val pieData = PieData(pieDataSet)
                pieData.setDrawValues(false)

                mediaStatsChart.statsPieChart.apply {
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

        private fun getStatusLabel(status: MediaListStatus?, mediaType: MediaType?): String {
            return status?.getString(if (mediaType == MediaType.MANGA) com.zen.alchan.helper.enums.MediaType.MANGA else com.zen.alchan.helper.enums.MediaType.ANIME) ?: ""
        }
    }

    inner class ScoreDistributionViewHolder(private val binding: ListStatsChartBarBinding) : ViewHolder(binding) {
        override fun bind(item: MediaStatsItem, index: Int) {
            with(binding) {
                val chart = ArrayList<Chart>()
                item.media.stats?.scoreDistribution?.forEach {
                    val hexColor = when (it.score) {
                        10 -> "#d2492d"
                        20 -> "#d2642c"
                        30 -> "#d2802e"
                        40 -> "#d29d2f"
                        50 -> "#d2b72e"
                        60 -> "#d3d22e"
                        70 -> "#b8d22c"
                        80 -> "#9cd42e"
                        90 -> "#81d12d"
                        100 -> "#63d42e"
                        else -> null
                    }
                    val label = it.score.getNumberFormatting()
                    val amount = it.amount.toDouble()

                    chart.add(Chart(hexColor, label, amount))
                }

                val useStringLabel = chart.any{ it.label.toDoubleOrNull() == null } ?: false

                val newValueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }

                val barEntries = chart.mapIndexed { index, chart ->
                    BarEntry(if (useStringLabel) index * 10F + 10F else (chart.label.toFloatOrNull() ?: index * 10F + 10F), chart.value.toFloat())
                }
                val barDataSet = BarDataSet(barEntries, "")
                barDataSet.colors = chart.map { if (it.color.isNullOrBlank()) context.getAttrValue(R.attr.themeSecondaryColor) else Color.parseColor(it.color) }

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

                    if (chart.isNotEmpty() && useStringLabel) {
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val labelIndex = round(value / 10.0) - 1
                                if (labelIndex >= 0 && labelIndex < chart.size) {
                                    return chart[labelIndex.toInt()].label
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
}