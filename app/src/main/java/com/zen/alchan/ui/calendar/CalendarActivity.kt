package com.zen.alchan.ui.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarActivity : BaseActivity() {

    private val viewModel by viewModel<CalendarViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        setSupportActionBar(toolbarLayout)
        supportActionBar?.apply {
            title = getString(R.string.calendar)
            setDisplayHomeAsUpEnabled(true)
        }

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {

    }

    private fun initLayout() {
        if (viewModel.startDate == null || viewModel.untilDate == null) {
            // set date
            // set text to date range
        } else {
            // set text to date range
        }

        dateRangeText.setOnClickListener {

        }

        showCurrentSeasonText.setOnClickListener {
            showCurrentSeasonCheckBox.performClick()
        }

        showCurrentSeasonCheckBox.setOnClickListener {

        }

        showOnListText.setOnClickListener {
            showOnListCheckBox.performClick()
        }

        showOnListCheckBox.setOnClickListener {

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}