package com.zen.alchan.ui.calendar

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.SearchRepository

class CalendarScheduleViewModel(val searchRepository: SearchRepository) : ViewModel() {

    val filteredAiringSchedule by lazy {
        searchRepository.filteredAiringSchedule
    }

    val scheduleList = ArrayList<AiringScheduleQuery.AiringSchedule>()
    var startDate = 0L

    fun filterList(list: List<AiringScheduleQuery.AiringSchedule>) {
        scheduleList.clear()

        val endDate = startDate + (24 * 3600 * 1000)
        val tempList = list.filter { it.airingAt * 1000L in startDate until endDate }

        scheduleList.addAll(tempList)
    }
}