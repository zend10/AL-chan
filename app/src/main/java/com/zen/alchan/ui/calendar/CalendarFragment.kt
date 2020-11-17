package com.zen.alchan.ui.calendar

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.zen.alchan.R
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.updateBottomPadding
import com.zen.alchan.ui.browse.BrowseActivity
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class CalendarFragment : Fragment() {

    private val viewModel by viewModel<CalendarScheduleViewModel>()

    companion object {
        const val START_DATE = "startDate"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        scheduleRecyclerView.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateBottomPadding(windowInsets, initialPadding)
        }

        viewModel.startDate = arguments?.getLong(START_DATE) ?: 0

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.filteredAiringSchedule.observe(viewLifecycleOwner, Observer {
            viewModel.filterList(it)
            initLayout()
        })
    }

    private fun initLayout() {
        if (viewModel.scheduleList.isNullOrEmpty()) {
            emptyLayout.visibility = View.VISIBLE
            scheduleRecyclerView.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.GONE
            scheduleRecyclerView.visibility = View.VISIBLE

            scheduleRecyclerView.adapter = CalendarScheduleRvAdapter(requireActivity(), viewModel.scheduleList, object : CalendarScheduleRvAdapter.CalendarScheduleListener {
                override fun openMedia(id: Int) {
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.ANIME.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, id)
                    startActivity(intent)
                }
            })
        }
    }
}