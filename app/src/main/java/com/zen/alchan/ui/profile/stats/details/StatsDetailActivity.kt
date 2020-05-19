package com.zen.alchan.ui.profile.stats.details

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.enums.StatsCategory
import com.zen.alchan.helper.pojo.UserStatsData
import com.zen.alchan.helper.removeTrailingZero
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_stats_detail.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import type.UserStatisticsSort
import kotlin.math.round

class StatsDetailActivity : BaseActivity() {

    private val viewModel by viewModel<StatsDetailViewModel>()

    private val pieColorList = Constant.PIE_CHART_COLOR_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats_detail)

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))

        setSupportActionBar(toolbarLayout)
        supportActionBar?.apply {
            title = getString(R.string.statistic_details)
            setDisplayHomeAsUpEnabled(true)
        }

        if (viewModel.selectedCategory == null) {
            viewModel.selectedCategory = StatsCategory.FORMAT
            viewModel.selectedMedia = MediaType.ANIME
            viewModel.selectedStatsSort = UserStatisticsSort.COUNT_DESC
        }

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.formatStatisticResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.currentStats = ArrayList()

                    if (viewModel.selectedMedia == MediaType.ANIME) {
                        it.data?.user?.statistics?.anime?.formats?.forEach { item ->
                            viewModel.currentStats?.add(UserStatsData(
                                color = pieColorList[viewModel.currentStats?.size!!],
                                count = item?.count,
                                meanScore = item?.meanScore,
                                minutesWatched = item?.minutesWatched,
                                mediaIds = item?.mediaIds,
                                label = item?.format?.name?.replaceUnderscore()
                            ))
                        }
                    } else {
                        it.data?.user?.statistics?.manga?.formats?.forEach { item ->
                            viewModel.currentStats?.add(UserStatsData(
                                color = pieColorList[viewModel.currentStats?.size!!],
                                count = item?.count,
                                meanScore = item?.meanScore,
                                chaptersRead = item?.chaptersRead,
                                mediaIds = item?.mediaIds,
                                label = item?.format?.name?.replaceUnderscore()
                            ))
                        }
                    }
                    setupStatistic()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        viewModel.statusStatisticResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.currentStats = ArrayList()

                    if (viewModel.selectedMedia == MediaType.ANIME) {
                        it.data?.user?.statistics?.anime?.statuses?.forEach { item ->
                            viewModel.currentStats?.add(UserStatsData(
                                color = if (Constant.STATUS_COLOR_MAP.containsKey(item?.status)) Constant.STATUS_COLOR_MAP[item?.status] else null,
                                count = item?.count,
                                meanScore = item?.meanScore,
                                minutesWatched = item?.minutesWatched,
                                mediaIds = item?.mediaIds,
                                label = item?.status?.name.replaceUnderscore()
                            ))
                        }
                    } else {
                        it.data?.user?.statistics?.manga?.statuses?.forEach { item ->
                            viewModel.currentStats?.add(UserStatsData(
                                color = if (Constant.STATUS_COLOR_MAP.containsKey(item?.status)) Constant.STATUS_COLOR_MAP[item?.status] else null,
                                count = item?.count,
                                meanScore = item?.meanScore,
                                chaptersRead = item?.chaptersRead,
                                mediaIds = item?.mediaIds,
                                label = item?.status?.name.replaceUnderscore()
                            ))
                        }
                    }
                    setupStatistic()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        viewModel.scoreStatisticResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.currentStats = ArrayList()

                    if (viewModel.selectedMedia == MediaType.ANIME) {
                        it.data?.user?.statistics?.anime?.scores?.forEach { item ->
                            viewModel.currentStats?.add(UserStatsData(
                                color = if (Constant.SCORE_COLOR_MAP.containsKey(item?.meanScore?.toInt())) Constant.SCORE_COLOR_MAP[item?.score?.toInt()] else null,
                                count = item?.count,
                                meanScore = item?.meanScore,
                                minutesWatched = item?.minutesWatched,
                                mediaIds = item?.mediaIds,
                                label = item?.meanScore?.removeTrailingZero()
                            ))
                        }
                    } else {
                        it.data?.user?.statistics?.manga?.scores?.forEach { item ->
                            viewModel.currentStats?.add(UserStatsData(
                                color = if (Constant.SCORE_COLOR_MAP.containsKey(item?.score)) Constant.SCORE_COLOR_MAP[item?.score] else null,
                                count = item?.count,
                                meanScore = item?.meanScore,
                                chaptersRead = item?.chaptersRead,
                                mediaIds = item?.mediaIds,
                                label = item?.meanScore?.removeTrailingZero()
                            ))
                        }
                    }
                    setupStatistic()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        viewModel.lengthStatisticResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.currentStats = ArrayList()

                    if (viewModel.selectedMedia == MediaType.ANIME) {
                        it.data?.user?.statistics?.anime?.lengths?.forEach { item ->
                            viewModel.currentStats?.add(UserStatsData(
                                color = pieColorList[viewModel.currentStats?.size!!],
                                count = item?.count,
                                meanScore = item?.meanScore,
                                minutesWatched = item?.minutesWatched,
                                mediaIds = item?.mediaIds,
                                label = item?.length
                            ))
                        }
                    } else {
                        it.data?.user?.statistics?.manga?.lengths?.forEach { item ->
                            viewModel.currentStats?.add(UserStatsData(
                                color = pieColorList[viewModel.currentStats?.size!!],
                                count = item?.count,
                                meanScore = item?.meanScore,
                                chaptersRead = item?.chaptersRead,
                                mediaIds = item?.mediaIds,
                                label = item?.length
                            ))
                        }
                    }
                    setupStatistic()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                }
            }
        })

        if (viewModel.currentStats == null) {
            viewModel.getStatisticData()
        } else {
            setupStatistic()
        }
    }

    private fun initLayout() {
        statsDetailsRefreshLayout.setOnRefreshListener {
            statsDetailsRefreshLayout.isRefreshing = false
            viewModel.getStatisticData()
        }

        statsCategoryText.text = viewModel.selectedCategory?.name
        statsMediaText.text = viewModel.selectedMedia?.name
        statsSortText.text = viewModel.getSortString()

        statsPieChart.visibility = View.GONE
        statsBarChart.visibility = View.GONE
        statsLineChart.visibility = View.GONE

        when (viewModel.selectedCategory) {
            StatsCategory.VOICE_ACTOR, StatsCategory.STUDIO -> statsMediaLayout.visibility = View.GONE
            StatsCategory.SCORE, StatsCategory.LENGTH -> statsSortLayout.visibility = View.GONE
            else -> {
                statsMediaLayout.visibility = View.VISIBLE
                statsSortLayout.visibility = View.VISIBLE
            }
        }

        statsCategoryText.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setItems(viewModel.getStatsCategoryArray()) { _, which ->
                    viewModel.selectedCategory = StatsCategory.values()[which]
                    statsCategoryText.text = viewModel.selectedCategory?.name.replaceUnderscore()
                    initLayout()
                    viewModel.getStatisticData()
                }
                .show()
        }

        statsMediaText.setOnClickListener {
            val mediaTypeArray = viewModel.getMediaTypeArray()
            MaterialAlertDialogBuilder(this)
                .setItems(mediaTypeArray) { _, which ->
                    viewModel.selectedMedia = MediaType.valueOf(mediaTypeArray[which])
                    statsMediaText.text = viewModel.selectedMedia?.name
                    initLayout()
                    viewModel.getStatisticData()
                }
                .show()
        }

        statsSortText.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setItems(viewModel.getSortDataArray()) { _, which ->
                    viewModel.selectedStatsSort = viewModel.sortDataList[which]
                    statsSortText.text = viewModel.getSortString()
                    initLayout()
                    viewModel.getStatisticData()
                }
                .show()
        }
    }

    private fun setupStatistic() {
        when (viewModel.selectedCategory) {
            StatsCategory.FORMAT -> handleFormatLayout()
            StatsCategory.STATUS -> handleStatusLayout()
            StatsCategory.SCORE -> handleScoreLayout()
            StatsCategory.LENGTH -> handleLengthLayout()
            StatsCategory.RELEASE_YEAR -> handleReleaseYearLayout()
            StatsCategory.START_YEAR -> handleStartYearLayout()
            StatsCategory.GENRE -> handleGenreLayout()
            StatsCategory.TAG -> handleTagLayout()
            StatsCategory.COUNTRY -> handleCountryLayout()
            StatsCategory.VOICE_ACTOR -> handleVoiceActorLayout()
            StatsCategory.STAFF -> handleStaffLayout()
            StatsCategory.STUDIO -> handleStudioLayout()
        }
    }

    private fun assignAdapter(): StatsDetailRvAdapter {
        return StatsDetailRvAdapter(this, viewModel.currentStats ?: ArrayList(), viewModel.selectedCategory!!, viewModel.selectedMedia!!, object : StatsDetailRvAdapter.StatsDetailListener {
            override fun passSelectedData(id: Int) {

            }
        })
    }

    private fun setupPieChart(pieDataSet: PieDataSet) {
        statsPieChart.visibility = View.VISIBLE
        statsBarChart.visibility = View.GONE
        statsLineChart.visibility = View.GONE

        statsPieChart.clear()
        statsPieChart.invalidate()
        statsPieChart.clear()

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)

        statsPieChart.apply {
            setHoleColor(ContextCompat.getColor(this@StatsDetailActivity, android.R.color.transparent))
            setDrawEntryLabels(false)
            setTouchEnabled(false)
            description.isEnabled = false
            legend.isEnabled = false
            data = pieData
            invalidate()
        }
    }

    private fun setupBarChart(barDataSet: BarDataSet, xAxisLabel: List<String>? = null, barWidth: Float? = null) {
        statsPieChart.visibility = View.GONE
        statsBarChart.visibility = View.VISIBLE
        statsLineChart.visibility = View.GONE

        statsBarChart.data?.clearValues()
        statsBarChart.xAxis.valueFormatter = null
        statsBarChart.notifyDataSetChanged()
        statsBarChart.clear()
        statsBarChart.invalidate()

        val newValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val barData = BarData(barDataSet)
        barData.setValueTextColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeContentColor))
        barData.barWidth = barWidth ?: 3F
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
            textColor = AndroidUtility.getResValueFromRefAttr(this@StatsDetailActivity, R.attr.themeContentColor)

            if (!xAxisLabel.isNullOrEmpty()) {
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = round(value / 10.0) - 1
                        if (index < xAxisLabel.size) {
                            return xAxisLabel[index.toInt()]
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

    private fun handleFormatLayout() {
        if (viewModel.selectedStatsSort == UserStatisticsSort.MEAN_SCORE_DESC) {
            statsRecyclerView.adapter = assignAdapter()
            return
        }

        val pieEntries = ArrayList<PieEntry>()

        viewModel.currentStats?.forEach {
            val progress = if (viewModel.selectedStatsSort == UserStatisticsSort.COUNT_DESC) {
                it.count?.toFloat()
            } else if (viewModel.selectedMedia == MediaType.ANIME) {
                it.minutesWatched?.toFloat()
            } else {
                it.chaptersRead?.toFloat()
            }
            pieEntries.add(PieEntry(progress!!, it.label))
        }

        val pieDataSet = PieDataSet(pieEntries, "Format Distribution")
        pieDataSet.colors = pieColorList

        setupPieChart(pieDataSet)

        statsRecyclerView.adapter = assignAdapter()
    }

    private fun handleStatusLayout() {
        if (viewModel.selectedStatsSort == UserStatisticsSort.MEAN_SCORE_DESC) {
            statsRecyclerView.adapter = assignAdapter()
            return
        }

        val pieEntries = ArrayList<PieEntry>()
        val sortedColorList = ArrayList<Int>()

        viewModel.currentStats?.forEach {
            val progress = if (viewModel.selectedStatsSort == UserStatisticsSort.COUNT_DESC) {
                it.count?.toFloat()
            } else if (viewModel.selectedMedia == MediaType.ANIME) {
                it.minutesWatched?.toFloat()
            } else {
                it.chaptersRead?.toFloat()
            }
            pieEntries.add(PieEntry(progress!!, it.label))
            sortedColorList.add(it.color ?: AndroidUtility.getResValueFromRefAttr(this, R.attr.themeContentColor))
        }

        val pieDataSet = PieDataSet(pieEntries, "Status Distribution")
        pieDataSet.colors = sortedColorList

        setupPieChart(pieDataSet)

        statsRecyclerView.adapter = assignAdapter()
    }

    private fun handleScoreLayout() {
        val barEntries = ArrayList<BarEntry>()
        val scoreList = arrayListOf(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
        val sortedStats = ArrayList<UserStatsData>()

        scoreList.forEach { score ->
            val scoreDetail = viewModel.currentStats?.find { it.meanScore?.toInt() == score }
            if (scoreDetail != null) {
                barEntries.add(BarEntry(score.toFloat(), scoreDetail.count?.toFloat()!!))
                sortedStats.add(scoreDetail)
            } else {
                barEntries.add(BarEntry(score.toFloat(), 0F))
                sortedStats.add(UserStatsData(
                    color = Constant.SCORE_COLOR_MAP[score],
                    count = 0,
                    meanScore = score.toDouble(),
                    minutesWatched = 0,
                    chaptersRead = 0,
                    label = score.toString())
                )
            }
        }

        viewModel.currentStats = sortedStats

        val barDataSet = BarDataSet(barEntries, "Score Distribution")
        barDataSet.colors = Constant.SCORE_COLOR_LIST

        setupBarChart(barDataSet)

        statsRecyclerView.adapter = assignAdapter()
    }

    private fun handleLengthLayout() {
        val barEntries = ArrayList<BarEntry>()
        val lengthList = if (viewModel.selectedMedia == MediaType.ANIME) {
            arrayListOf("1", "2-6", "7-16", "17-28", "29-55", "56-100", "101+", "Unknown")
        } else {
            arrayListOf("1", "2-10", "11-25", "26-50", "51-100", "101-200", "201+", "Unknown")
        }
        val sortedStats = ArrayList<UserStatsData>()

        lengthList.forEachIndexed { index, length ->
            val lengthDetail = if (index != lengthList.lastIndex) {
                viewModel.currentStats?.find { it.label == length }
            } else {
                viewModel.currentStats?.find { it.label == null }
            }
            if (index == lengthList.lastIndex) {
                lengthDetail?.label = length
            }
            if (lengthDetail != null) {
                lengthDetail.color = Constant.SCORE_COLOR_LIST[index]
                barEntries.add(BarEntry(index.toFloat() * 10 + 10, lengthDetail.count?.toFloat()!!))
                sortedStats.add(lengthDetail)
            } else {
                barEntries.add(BarEntry(index.toFloat() * 10 + 10, 0F))
                sortedStats.add(UserStatsData(
                    color = Constant.PIE_CHART_COLOR_LIST[index],
                    count = 0,
                    meanScore = 0.0,
                    minutesWatched = 0,
                    chaptersRead = 0,
                    label = length)
                )
            }
        }

        viewModel.currentStats = sortedStats

        val barDataSet = BarDataSet(barEntries, "Length Distribution")
        barDataSet.colors = Constant.SCORE_COLOR_LIST

        setupBarChart(barDataSet, lengthList)

        statsRecyclerView.adapter = assignAdapter()
    }

    private fun handleReleaseYearLayout() {}

    private fun handleStartYearLayout() {}

    private fun handleGenreLayout() {}

    private fun handleTagLayout() {}

    private fun handleCountryLayout() {}

    private fun handleVoiceActorLayout() {}

    private fun handleStaffLayout() {}

    private fun handleStudioLayout() {}

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
