package com.zen.alchan.ui.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaExternalLink
import com.zen.alchan.databinding.FragmentCalendarBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class CalendarFragment : BaseFragment<FragmentCalendarBinding, CalendarViewModel>() {

    override val viewModel: CalendarViewModel by viewModel()

    private var adapter: CalendarRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCalendarBinding {
        return FragmentCalendarBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.calendar))

            adapter = CalendarRvAdapter(requireContext(), listOf(), AppSetting(), getCalendarListener())
            calendarRecyclerView.adapter = adapter

            calendarSwipeRefresh.setOnRefreshListener {
                viewModel.reloadCalendar()
            }

            calendarDateText.clicks {
                viewModel.loadDateCalendar()
            }

            calendarShowOnListCheckBox.setOnClickListener {
                viewModel.updateShowOnlyWatchingAndPlanning(calendarShowOnListCheckBox.isChecked)
            }

            calendarShowCurrentSeasonCheckBox.setOnClickListener {
                viewModel.updateShowOnlyCurrentSeason(calendarShowCurrentSeasonCheckBox.isChecked)
            }

            seriesShowAdultContentCheckBox.setOnClickListener {
                viewModel.updateShowAdult(seriesShowAdultContentCheckBox.isChecked)
            }
        }
    }

    override fun setUpInsets() {
        with(binding) {
            defaultToolbar.defaultToolbar.applyTopPaddingInsets()
            calendarRecyclerView.applySidePaddingInsets()
            calendarFilterLayout.applyBottomSidePaddingInsets()
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.calendarSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.appSetting.subscribe {
                adapter = CalendarRvAdapter(requireContext(), listOf(), it, getCalendarListener())
                binding.calendarRecyclerView.adapter = adapter
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.airingSchedules.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.date.subscribe {
                binding.calendarDateText.text = TimeUtil.displayInDateFormat(it)
            },
            viewModel.showOnlyWatchingAndPlanning.subscribe {
                binding.calendarShowOnListCheckBox.isChecked = it
            },
            viewModel.showOnlyCurrentSeason.subscribe {
                binding.calendarShowCurrentSeasonCheckBox.isChecked = it
            },
            viewModel.showAdult.subscribe {
                binding.seriesShowAdultContentCheckBox.isChecked = it
            },
            viewModel.calendarDate.subscribe {
                dialog.showDatePicker(it) { year, month, dayOfMonth ->
                    viewModel.updateDate(dayOfMonth, month, year)
                }
            }
        )

        viewModel.loadData(Unit)
    }

    private fun getCalendarListener(): CalendarRvAdapter.CalendarListener {
        return object : CalendarRvAdapter.CalendarListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }

            override fun navigateToUrl(mediaExternalLink: MediaExternalLink) {
                navigation.openWebView(mediaExternalLink.url)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}