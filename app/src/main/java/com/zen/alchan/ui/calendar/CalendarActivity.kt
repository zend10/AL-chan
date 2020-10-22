package com.zen.alchan.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.zen.alchan.R
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.browse.BrowseActivity
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarActivity : BaseActivity() {

    private val viewModel by viewModel<CalendarViewModel>()

    private lateinit var dateAdapter: CalendarDateRvAdapter
    private lateinit var scheduleAdapter: CalendarScheduleRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))
        setSupportActionBar(toolbarLayout)
        supportActionBar?.apply {
            title = getString(R.string.calendar)
            setDisplayHomeAsUpEnabled(true)
        }

        dateAdapter = assignDateAdapter()
        dateRecyclerView.adapter = dateAdapter

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.airingScheduleResponse.observe(this, androidx.lifecycle.Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    if (!viewModel.hasNextPage) {
                        loadingLayout.visibility = View.GONE
                        return@Observer
                    }

                    viewModel.hasNextPage = it.data?.page?.pageInfo?.hasNextPage ?: false
                    viewModel.page += 1
                    viewModel.isInit = true
                    viewModel.scheduleList.addAll(it.data?.page?.airingSchedules?.filterNotNull() ?: listOf())

                    if (viewModel.hasNextPage) {
                        viewModel.getAiringSchedule()
                    } else {
                        loadingLayout.visibility = View.GONE
                        assignScheduleAdapter()
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(this, it.message)
                    assignScheduleAdapter()
                }
            }
        })

        if (!viewModel.isInit) {
            viewModel.getAiringSchedule()
        } else {
            assignScheduleAdapter()
        }
    }

    private fun initLayout() {
        calendarRefreshLayout.setOnRefreshListener {
            calendarRefreshLayout.isRefreshing = false

            dateAdapter = assignDateAdapter()
            dateRecyclerView.adapter = dateAdapter

            viewModel.hasNextPage = true
            viewModel.page = 1
            viewModel.getAiringSchedule()
        }
    }

    private fun assignDateAdapter(): CalendarDateRvAdapter {
        viewModel.setDateList()

        return CalendarDateRvAdapter(this, viewModel.dateList, object : CalendarDateRvAdapter.CalendarDateListener {
            override fun passSelectedDate(position: Int) {
                viewModel.dateList.forEach { it.isSelected = false }
                viewModel.dateList[position].isSelected = true
                dateAdapter.notifyDataSetChanged()
                assignScheduleAdapter()
            }
        })
    }

    private fun assignScheduleAdapter() {
        viewModel.filterList()

        scheduleAdapter = CalendarScheduleRvAdapter(this, viewModel.filteredList, object : CalendarScheduleRvAdapter.CalendarScheduleListener {
            override fun openMedia(id: Int) {
                val intent = Intent(this@CalendarActivity, BrowseActivity::class.java)
                intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.ANIME.name)
                intent.putExtra(BrowseActivity.LOAD_ID, id)
                startActivity(intent)
            }
        })
        scheduleRecyclerView.adapter = scheduleAdapter

        if (viewModel.filteredList.isNullOrEmpty()) {
            scheduleRecyclerView.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        } else {
            scheduleRecyclerView.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemFilter) {
            val dialog = CalendarFilterBottomSheetDialog()
            dialog.setListener(object : CalendarFilterBottomSheetDialog.CalendarFilterListener {
                override fun passFilterData(
                    showOnlyInList: Boolean,
                    showOnlyCurrentSeason: Boolean,
                    showAdultContent: Boolean
                ) {
                    viewModel.showOnlyOnWatchingAndPlanning = showOnlyInList
                    viewModel.showOnlyCurrentSeason = showOnlyCurrentSeason
                    viewModel.showAdult = showAdultContent
                    assignScheduleAdapter()
                }
            })

            val bundle = Bundle()
            bundle.putBoolean(CalendarFilterBottomSheetDialog.SHOW_ONLY_IN_LIST, viewModel.showOnlyOnWatchingAndPlanning)
            bundle.putBoolean(CalendarFilterBottomSheetDialog.SHOW_ONLY_CURRENT_SEASON, viewModel.showOnlyCurrentSeason)
            bundle.putBoolean(CalendarFilterBottomSheetDialog.SHOW_ADULT_CONTENT, viewModel.showAdult)

            dialog.arguments = bundle
            dialog.show(supportFragmentManager, null)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}