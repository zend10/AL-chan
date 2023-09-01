package com.zen.alchan.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaExternalLink
import com.zen.alchan.databinding.FragmentCalendarDayBinding
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseFragment
import io.reactivex.rxjava3.core.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarDayFragment(
    private val date: Int,
    private val calendarSettings: Observable<CalendarSettings>
) : BaseFragment<FragmentCalendarDayBinding, CalendarDayViewModel>() {

    override val viewModel: CalendarDayViewModel by viewModel()

    private var adapter: CalendarRvAdapter? = null

    override fun generateViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCalendarDayBinding {
        return FragmentCalendarDayBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            adapter = CalendarRvAdapter(requireContext(), listOf(), AppSetting(), getCalendarListener())
            calendarDayRecyclerView.adapter = adapter

            calendarDaySwipeRefresh.setOnRefreshListener {
                calendarDaySwipeRefresh.isRefreshing = false
            }
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.airingSchedules.subscribe {
                adapter?.updateData(it, true);
            },
            viewModel.loading.subscribe {
                binding.calendarDaySwipeRefresh.isRefreshing = it
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.appSetting.subscribe {
                adapter = CalendarRvAdapter(requireContext(), listOf(), it, getCalendarListener())
                binding.calendarDayRecyclerView.adapter = adapter
            },
            calendarSettings.subscribe {
                viewModel.updateCalendarSettings(it)
            }
        )

        viewModel.loadData(CalendarDayParam(date))
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
        fun newInstance(date: Int, calendarSettings: Observable<CalendarSettings>) = CalendarDayFragment(date, calendarSettings)
    }
}