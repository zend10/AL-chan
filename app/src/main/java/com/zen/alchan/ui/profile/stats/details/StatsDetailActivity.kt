package com.zen.alchan.ui.profile.stats.details

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.enums.StatsCategory
import com.zen.alchan.helper.pojo.UserStatsData
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
                                statsCategory = viewModel.selectedCategory!!,
                                mediaType = viewModel.selectedMedia!!,
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
                                statsCategory = viewModel.selectedCategory!!,
                                mediaType = viewModel.selectedMedia!!,
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
                                statsCategory = viewModel.selectedCategory!!,
                                mediaType = viewModel.selectedMedia!!,
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
                                statsCategory = viewModel.selectedCategory!!,
                                mediaType = viewModel.selectedMedia!!,
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

        when (viewModel.selectedCategory) {
            StatsCategory.VOICE_ACTOR, StatsCategory.STUDIO -> statsMediaLayout.visibility = View.GONE
            else -> statsMediaLayout.visibility = View.VISIBLE
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
        return StatsDetailRvAdapter(this, viewModel.currentStats ?: ArrayList(), object : StatsDetailRvAdapter.StatsDetailListener {
            override fun passSelectedData(id: Int) {

            }
        })
    }

    private fun setupPieChart(pieDataSet: PieDataSet) {
        statsPieChart.visibility = View.VISIBLE

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

    private fun handleFormatLayout() {
        statsPieChart.visibility = View.GONE

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
        statsPieChart.visibility = View.GONE

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

    private fun handleScoreLayout() {}

    private fun handleLengthLayout() {}

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
