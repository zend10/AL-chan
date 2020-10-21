package com.zen.alchan.ui.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.DateItem
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CalendarActivity : BaseActivity() {

    private val viewModel by viewModel<CalendarViewModel>()

    private lateinit var dateAdapter: CalendarDateRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

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
                    loadingLayout.visibility = View.GONE
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                }
            }
        })

        viewModel.getAiringSchedule()
    }

    private fun initLayout() {
        calendarRefreshLayout.setOnRefreshListener {
            calendarRefreshLayout.isRefreshing = false

            dateAdapter = assignDateAdapter()
            dateRecyclerView.adapter = dateAdapter

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
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}