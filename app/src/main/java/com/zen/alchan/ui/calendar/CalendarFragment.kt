package com.zen.alchan.ui.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaExternalLink
import com.zen.alchan.databinding.FragmentCalendarBinding
import com.zen.alchan.databinding.FragmentCalendarDayBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.sql.Time
import java.util.Calendar


class CalendarFragment : BaseFragment<FragmentCalendarBinding, CalendarViewModel>() {

    override val viewModel: CalendarViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCalendarBinding {
        return FragmentCalendarBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.calendar))

            var a = object : FragmentStateAdapter(this@CalendarFragment) {
                override fun getItemCount(): Int {
                    return Int.MAX_VALUE
                }

                override fun createFragment(position: Int): Fragment {
                    return CalendarDayFragment(getDateFromPagePosition(position), viewModel.calendarSettings);
                }
            }

            calendarPager.adapter = a;
            calendarPager.setCurrentItem(Int.MAX_VALUE / 2, false)
            calendarPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.updateDate(getDateFromPagePosition(position))
                }
            })


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
            calendarPager.applySidePaddingInsets()
            calendarFilterLayout.applyBottomSidePaddingInsets()
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.date.subscribe {
                binding.calendarDateText.text = TimeUtil.displayInDateFormat(it)
            },
            viewModel.calendarSettings.subscribe {
                binding.calendarShowOnListCheckBox.isChecked = it.showOnlyWatchingAndPlanning
                binding.calendarShowCurrentSeasonCheckBox.isChecked = it.showOnlyCurrentSeason
                binding.seriesShowAdultContentCheckBox.isChecked = it.showAdult
            },
            viewModel.calendarDate.subscribe {
                dialog.showDatePicker(it) { year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    calendar.set(Calendar.MONTH, month - 1)
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    binding.calendarPager.currentItem = getPagePositionFromDate(calendar)
                }
            }
        )

        viewModel.loadData(Unit)
    }
    private fun getDateFromPagePosition(position: Int) : Int {
        val middle = Int.MAX_VALUE / 2
        val pageDifference = position - middle
        return TimeUtil.getTodayInSeconds() + (3600 * 24) * pageDifference;
    }

    private fun getPagePositionFromDate(calendar: Calendar): Int {
        // Thanks, ChatGPT my brain was to smol for this
        val dateInSeconds = (calendar.timeInMillis / 1000L).toInt()
        val todayInSeconds = TimeUtil.getTodayInSeconds()
        val middle = Int.MAX_VALUE / 2
        val pageDifference = (dateInSeconds - todayInSeconds) / (3600 * 24)
        return middle + pageDifference
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}