package com.zen.alchan.ui.profile.stats


import android.content.Intent
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
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.StatusDistributionItem
import com.zen.alchan.helper.roundToTwoDecimal
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.media.stats.MediaStatsStatusRvAdapter
import com.zen.alchan.ui.browse.user.UserFragment
import com.zen.alchan.ui.common.ChartDialog
import com.zen.alchan.ui.profile.stats.details.StatsDetailActivity
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus
import kotlin.math.round

/**
 * A simple [Fragment] subclass.
 */
class StatsFragment : BaseFragment() {

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

        if (arguments != null && arguments?.getInt(UserFragment.USER_ID) != null && arguments?.getInt(UserFragment.USER_ID) != 0) {
            viewModel.otherUserId = arguments?.getInt(UserFragment.USER_ID)
        }

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.getStatisticObserver().observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.userStats = it.data?.user?.statistics
                    viewModel.scoreFormat = it.data?.user?.mediaListOptions?.scoreFormat
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
            if (viewModel.otherUserId != null) {
                listener?.changeFragment(BrowsePage.USER_STATS_DETAIL, viewModel.otherUserId!!)
            } else {
                startActivity(Intent(activity, StatsDetailActivity::class.java))
            }
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

            val pieDataSet = PieDataSet(pieEntries, "Status Distribution")
            pieDataSet.colors = Constant.STATUS_COLOR_LIST

            if (!viewModel.showStatsAutomatically) {
                animeStatsStatusShowButton.visibility = View.VISIBLE
                animeStatsStatusChartLayout.visibility = View.GONE

                animeStatsStatusShowButton.setOnClickListener {
                    openPieChartDialog(pieDataSet)
                }
            } else {
                showAnimeStatsStatusChart(pieDataSet)
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

            val pieDataSet = PieDataSet(pieEntries, "Status Distribution")
            pieDataSet.colors = Constant.STATUS_COLOR_LIST

            if (!viewModel.showStatsAutomatically) {
                mangaStatsStatusShowButton.visibility = View.VISIBLE
                mangaStatsStatusChartLayout.visibility = View.GONE

                mangaStatsStatusShowButton.setOnClickListener {
                    openPieChartDialog(pieDataSet)
                }
            } else {
                showMangaStatsStatusChart(pieDataSet)
            }

            mangaStatsStatusRecyclerView.adapter = MediaStatsStatusRvAdapter(activity!!, mangaStatusDistributionList)
        } else {
            mangaStatsStatusLayout.visibility = View.GONE
        }
    }

    private fun showAnimeStatsStatusChart(pieDataSet: PieDataSet) {
        animeStatsStatusShowButton.visibility = View.GONE
        animeStatsStatusChartLayout.visibility = View.VISIBLE

        try {
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
        } catch (e: Exception) {
            DialogUtility.showToast(activity, e.localizedMessage)
        }
    }

    private fun showMangaStatsStatusChart(pieDataSet: PieDataSet) {
        mangaStatsStatusShowButton.visibility = View.GONE
        mangaStatsStatusChartLayout.visibility = View.VISIBLE

        try {
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
        } catch (e: Exception) {
            DialogUtility.showToast(activity, e.localizedMessage)
        }
    }

    private fun openPieChartDialog(pieDataSet: PieDataSet) {
        val dialog = ChartDialog()
        val bundle = Bundle()
        bundle.putString(ChartDialog.PIE_ENTRIES, viewModel.gson.toJson(pieDataSet))
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }

    private fun handleScoreDistribution() {
        val stats = viewModel.userStats!!
        val scoreList = (10..100 step 10).toList()
        val valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        if (stats.anime?.scores?.isNullOrEmpty() == false) {
            animeStatsScoreLayout.visibility = View.VISIBLE

            val userScoreList = HashMap<Int, Int>()
            stats.anime.scores.forEach {
                val score = (round(it?.meanScore!! / 10.0) * 10).toInt()
                if (userScoreList.containsKey(score)) {
                    userScoreList[score] = userScoreList[score]!! + it.count
                } else {
                    userScoreList[score] = it.count
                }
            }

            val barEntries = ArrayList<BarEntry>()
            scoreList.forEach {  score ->
                if (userScoreList.containsKey(score)) {
                    barEntries.add(BarEntry(score.toFloat(), userScoreList[score]!!.toFloat()))
                } else {
                    barEntries.add(BarEntry(score.toFloat(), 0F))
                }
            }

            val barDataSet = BarDataSet(barEntries, "Score Distribution")
            barDataSet.colors = Constant.SCORE_COLOR_LIST

            if (!viewModel.showStatsAutomatically) {
                animeStatsScoreShowButton.visibility = View.VISIBLE
                animeStatsScoreChartLayout.visibility = View.GONE

                animeStatsScoreShowButton.setOnClickListener {
                    openBarChartDialog(barDataSet)
                }
            } else {
                showAnimeStatsScoreChart(valueFormatter, barDataSet)
            }
        } else {
            animeStatsScoreLayout.visibility = View.GONE
        }

        if (stats.manga?.scores?.isNullOrEmpty() == false) {
            mangaStatsScoreLayout.visibility = View.VISIBLE

            val userScoreList = HashMap<Int, Int>()
            stats.manga.scores.forEach {
                val score = (round(it?.meanScore!! / 10.0) * 10).toInt()
                if (userScoreList.containsKey(score)) {
                    userScoreList[score] = userScoreList[score]!! + it.count
                } else {
                    userScoreList[score] = it.count
                }
            }

            val barEntries = ArrayList<BarEntry>()
            scoreList.forEach {  score ->
                if (userScoreList.containsKey(score)) {
                    barEntries.add(BarEntry(score.toFloat(), userScoreList[score]!!.toFloat()))
                } else {
                    barEntries.add(BarEntry(score.toFloat(), 0F))
                }
            }

            val barDataSet = BarDataSet(barEntries, "Score Distribution")
            barDataSet.colors = Constant.SCORE_COLOR_LIST

            if (!viewModel.showStatsAutomatically) {
                mangaStatsScoreShowButton.visibility = View.VISIBLE
                mangaStatsScoreChartLayout.visibility = View.GONE

                mangaStatsScoreShowButton.setOnClickListener {
                    openBarChartDialog(barDataSet)
                }
            } else {
                showMangaStatsScoreChart(valueFormatter, barDataSet)
            }
        } else {
            mangaStatsScoreLayout.visibility = View.GONE
        }
    }

    private fun showAnimeStatsScoreChart(valueFormatter: ValueFormatter, barDataSet: BarDataSet) {
        animeStatsScoreShowButton.visibility = View.GONE
        animeStatsScoreChartLayout.visibility = View.VISIBLE

        try {
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
                setLabelCount(barDataSet.entryCount, true)
                textColor = AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor)
            }

            animeStatsScoreBarChart.apply {
                setTouchEnabled(false)
                description.isEnabled = false
                legend.isEnabled = false
                data = barData
                invalidate()
            }
        } catch (e: Exception) {
            DialogUtility.showToast(activity, e.localizedMessage)
        }
    }

    private fun showMangaStatsScoreChart(valueFormatter: ValueFormatter, barDataSet: BarDataSet) {
        mangaStatsScoreShowButton.visibility = View.GONE
        mangaStatsScoreChartLayout.visibility = View.VISIBLE

        try {
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
                setLabelCount(barDataSet.entryCount, true)
                textColor = AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor)
            }

            mangaStatsScoreBarChart.apply {
                setTouchEnabled(false)
                description.isEnabled = false
                legend.isEnabled = false
                data = barData
                invalidate()
            }
        } catch (e: Exception) {
            DialogUtility.showToast(activity, e.localizedMessage)
        }
    }

    private fun openBarChartDialog(barDataSet: BarDataSet) {
        val dialog = ChartDialog()
        val bundle = Bundle()
        bundle.putString(ChartDialog.BAR_ENTRIES, viewModel.gson.toJson(barDataSet))
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }
}
