package com.zen.alchan.ui.calendar

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.SearchRepository
import com.zen.alchan.helper.pojo.DateItem
import type.MediaSeason
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel(private val searchRepository: SearchRepository) : ViewModel() {

    var page = 1
    var hasNextPage = true
    var isInit = false
    var scheduleList = ArrayList<AiringScheduleQuery.AiringSchedule?>()
    var dateList = ArrayList<DateItem>()

    val airingScheduleResponse by lazy {
        searchRepository.airingScheduleResponse
    }

    fun getAiringSchedule() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.timeInMillis / 1000
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        val untilDate = calendar.timeInMillis / 1000

        searchRepository.getAiringSchedule(page, startDate.toInt(), untilDate.toInt())
    }

    fun setDateList() {
        dateList.clear()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        while (dateList.size < 7) {
            dateList.add(DateItem(calendar.timeInMillis, false))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        dateList[0].isSelected = true
    }
}