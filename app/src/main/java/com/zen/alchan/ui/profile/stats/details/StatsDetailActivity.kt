package com.zen.alchan.ui.profile.stats.details

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.zen.alchan.R
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.enums.StatsCategory
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
                    // TODO: convert to UserStatsData
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
        statsCategoryText.text = viewModel.selectedCategory?.name
        statsMediaText.text = viewModel.selectedMedia?.name
        statsDataText.text = viewModel.getDataString()

        when (viewModel.selectedCategory) {
            StatsCategory.VOICE_ACTOR, StatsCategory.STUDIO -> statsMediaLayout.visibility = View.GONE
            else -> statsMediaLayout.visibility = View.VISIBLE
        }

        statsCategoryText.setOnClickListener {

        }

        statsMediaText.setOnClickListener {

        }

        statsDataText.setOnClickListener {

        }
    }

    private fun setupStatistic() {

    }

    override fun onNavigateUp(): Boolean {
        finish()
        return super.onNavigateUp()
    }
}
