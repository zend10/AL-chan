package com.zen.alchan.ui.calendar

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.SearchRepository
import type.MediaSeason

class CalendarViewModel(private val searchRepository: SearchRepository) : ViewModel() {

    var page = 1
    var hasNextPage = true
    var isInit = false
    var scheduleList = ArrayList<AiringScheduleQuery.AiringSchedule?>()

    var currentSeason: MediaSeason? = null
    var startDate: Int? = null
    var untilDate: Int? = null

    val airingScheduleResponse by lazy {
        searchRepository.airingScheduleResponse
    }

    fun getAiringSchedule() {
        if (startDate != null && untilDate != null) {
            searchRepository.getAiringSchedule(page, startDate!!, untilDate!!)
        }
    }
}