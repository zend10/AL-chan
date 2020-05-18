package com.zen.alchan.ui.profile.stats


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.StatusDistributionItem
import com.zen.alchan.helper.removeTrailingZero
import com.zen.alchan.helper.roundToTwoDecimal
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.browse.media.stats.MediaStatsFragment
import com.zen.alchan.ui.browse.media.stats.MediaStatsStatusRvAdapter
import kotlinx.android.synthetic.main.dialog_set_progress.*
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus

/**
 * A simple [Fragment] subclass.
 */
class StatsFragment : Fragment() {

    private val viewModel by viewModel<StatsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.userStatisticsResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.userStats = it.data?.user?.statistics
                    initLayout()
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(activity, it.message)
                    loadingLayout.visibility = View.GONE
                }
            }
        })

        if (viewModel.userStats == null) {
            viewModel.getStatistics()
        } else {
            initLayout()
        }
    }

    private fun initLayout() {
        if (viewModel.userStats == null) {
            emptyLayout.visibility = View.VISIBLE
            statsLayout.visibility = View.GONE
            return
        }

        emptyLayout.visibility = View.GONE
        statsLayout.visibility = View.VISIBLE

        viewDetailLayout.setOnClickListener {
            // TODO: open detail statistic
        }

        handleGeneralStats()
        handleStatusDistribution()
        handleScoreDistribution()
    }

    private fun handleGeneralStats() {
        val stats = viewModel.userStats!!
        statsTotalAnimeText.text = stats.anime?.count?.toString() ?: "0"
        statsEpisodesWatchedText.text = stats.anime?.episodesWatched?.toString() ?: "0"
        statsDaysWatchedText.text = ((stats.anime?.minutesWatched ?: 0) / 60.0 / 24.0).roundToTwoDecimal()
        statsAnimeMeanScoreText.text = stats.anime?.meanScore?.toString() ?: "0"

        statsTotalMangaText.text = stats.manga?.count?.toString() ?: "0"
        statsChaptersReadText.text = stats.manga?.chaptersRead?.toString() ?: "0"
        statsVolumesReadText.text = stats.manga?.volumesRead?.toString() ?: "0"
        statsMangaMeanScoreText.text = stats.manga?.meanScore?.toString() ?: "0"
    }

    private fun handleStatusDistribution() {
        val stats = viewModel.userStats!!
        val statusList = arrayListOf(MediaListStatus.CURRENT, MediaListStatus.PLANNING, MediaListStatus.COMPLETED, MediaListStatus.DROPPED, MediaListStatus.PAUSED)

        if (stats.anime?.statuses?.isNullOrEmpty() == false) {
            animeStatsStatusLayout.visibility = View.VISIBLE

            val animeStatusDistributionList = ArrayList<StatusDistributionItem>()

            val pieEntries = ArrayList<PieEntry>()
            statusList.forEach { status ->
                val statusDetail = stats.anime.statuses.find { it?.status == status }
                val count = statusDetail?.count?.toFloat() ?: 0F
                pieEntries.add(PieEntry(count, status.name))
                animeStatusDistributionList.add(StatusDistributionItem(status.name, count.toInt(), Constant.STATUS_COLOR_LIST[animeStatusDistributionList.size]))
            }

            val pieDataSet = PieDataSet(pieEntries, "Score Distribution")
            pieDataSet.colors = Constant.STATUS_COLOR_LIST

            val pieData = PieData(pieDataSet)
            pieData.setDrawValues(false)

            animeStatsStatusPieChart.apply {
                setHoleColor(ContextCompat.getColor(activity!!, android.R.color.transparent))
                setDrawEntryLabels(false)
                setTouchEnabled(false)
                description.isEnabled = false
                legend.isEnabled = false
                data = pieData
                invalidate()
            }

            animeStatsStatusRecyclerView.adapter = MediaStatsStatusRvAdapter(activity!!, animeStatusDistributionList)
        } else {
            animeStatsStatusLayout.visibility = View.GONE
        }

        if (stats.manga?.statuses?.isNullOrEmpty() == false) {
            mangaStatsStatusLayout.visibility = View.VISIBLE

            val mangaStatusDistributionList = ArrayList<StatusDistributionItem>()

            val pieEntries = ArrayList<PieEntry>()
            statusList.forEach { status ->
                val statusDetail = stats.manga.statuses.find { it?.status == status }
                val count = statusDetail?.count?.toFloat() ?: 0F
                pieEntries.add(PieEntry(count, status.name))
                mangaStatusDistributionList.add(StatusDistributionItem(status.name, count.toInt(), Constant.STATUS_COLOR_LIST[mangaStatusDistributionList.size]))
            }

            val pieDataSet = PieDataSet(pieEntries, "Score Distribution")
            pieDataSet.colors = Constant.STATUS_COLOR_LIST

            val pieData = PieData(pieDataSet)
            pieData.setDrawValues(false)

            mangaStatsStatusPieChart.apply {
                setHoleColor(ContextCompat.getColor(activity!!, android.R.color.transparent))
                setDrawEntryLabels(false)
                setTouchEnabled(false)
                description.isEnabled = false
                legend.isEnabled = false
                data = pieData
                invalidate()
            }

            mangaStatsStatusRecyclerView.adapter = MediaStatsStatusRvAdapter(activity!!, mangaStatusDistributionList)
        } else {
            mangaStatsStatusLayout.visibility = View.GONE
        }
    }

    private fun handleScoreDistribution() {
        val stats = viewModel.userStats!!
        val scoreList = arrayListOf(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
        val valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        if (stats.anime?.scores?.isNullOrEmpty() == false) {
            animeStatsScoreLayout.visibility = View.VISIBLE

            val barEntries = ArrayList<BarEntry>()
            scoreList.forEach {  score ->
                val scoreDetail = stats.anime.scores.find { it?.meanScore?.toInt() == score }
                if (scoreDetail != null) {
                    barEntries.add(BarEntry(score.toFloat(), scoreDetail.count.toFloat()))
                } else {
                    barEntries.add(BarEntry(score.toFloat(), 0F))
                }
            }

            val barDataSet = BarDataSet(barEntries, "Score Distribution")
            barDataSet.colors = Constant.SCORE_COLOR_LIST

            val barData = BarData(barDataSet)
            barData.setValueTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
            barData.barWidth = 3F
            barData.setValueFormatter(valueFormatter)

            animeStatsScoreBarChart.axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }

            animeStatsScoreBarChart.axisRight.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }

            animeStatsScoreBarChart.xAxis.apply {
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                setLabelCount(barEntries.size, true)
                textColor = AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor)
            }

            animeStatsScoreBarChart.apply {
                setTouchEnabled(false)
                description.isEnabled = false
                legend.isEnabled = false
                data = barData
                invalidate()
            }
        } else {
            animeStatsScoreLayout.visibility = View.GONE
        }

        if (stats.manga?.scores?.isNullOrEmpty() == false) {
            mangaStatsScoreLayout.visibility = View.VISIBLE

            val barEntries = ArrayList<BarEntry>()
            scoreList.forEach {  score ->
                val scoreDetail = stats.manga.scores.find { it?.meanScore?.toInt() == score }
                if (scoreDetail != null) {
                    barEntries.add(BarEntry(score.toFloat(), scoreDetail.count.toFloat()))
                } else {
                    barEntries.add(BarEntry(score.toFloat(), 0F))
                }
            }

            val barDataSet = BarDataSet(barEntries, "Score Distribution")
            barDataSet.colors = Constant.SCORE_COLOR_LIST

            val barData = BarData(barDataSet)
            barData.setValueTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
            barData.barWidth = 3F
            barData.setValueFormatter(valueFormatter)

            mangaStatsScoreBarChart.axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }

            mangaStatsScoreBarChart.axisRight.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }

            mangaStatsScoreBarChart.xAxis.apply {
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                setLabelCount(barEntries.size, true)
                textColor = AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor)
            }

            mangaStatsScoreBarChart.apply {
                setTouchEnabled(false)
                description.isEnabled = false
                legend.isEnabled = false
                data = barData
                invalidate()
            }
        } else {
            mangaStatsScoreLayout.visibility = View.GONE
        }
    }
}
