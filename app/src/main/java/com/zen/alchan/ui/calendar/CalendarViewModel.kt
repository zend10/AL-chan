package com.zen.alchan.ui.calendar

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.AiringSchedule
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.zen.alchan.type.ExternalLinkType
import com.zen.alchan.type.MediaListStatus
import java.util.Calendar

class CalendarViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : BaseViewModel<Unit>() {

    private val _date = BehaviorSubject.createDefault(TimeUtil.getTodayInSeconds())
    val date: Observable<Int>
        get() = _date

    private val _calendarSettings = BehaviorSubject.createDefault(CalendarSettings())
    val calendarSettings: Observable<CalendarSettings>
        get() = _calendarSettings

    private val _calendarDate = PublishSubject.create<Calendar>()
    val calendarDate: Observable<Calendar>
        get() = _calendarDate

    override fun loadData(param: Unit) {
    }

    fun loadDateCalendar() {
        val dateInSeconds = _date.value ?: TimeUtil.getTodayInSeconds()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInSeconds * 1000L
        _calendarDate.onNext(calendar)
    }

    fun updateDate(date: Int) {
        _date.onNext(date)
    }

    fun updateShowOnlyWatchingAndPlanning(shouldShowOnlyWatchingAndPlanning: Boolean) {
        val currentSettings: CalendarSettings = _calendarSettings.value ?: CalendarSettings()
        _calendarSettings.onNext(CalendarSettings(shouldShowOnlyWatchingAndPlanning,
            currentSettings.showOnlyCurrentSeason,
            currentSettings.showAdult))
    }

    fun updateShowOnlyCurrentSeason(shouldShowOnlyCurrentSeason: Boolean) {
        val currentSettings: CalendarSettings = _calendarSettings.value ?: CalendarSettings()
        _calendarSettings.onNext(CalendarSettings(currentSettings.showOnlyWatchingAndPlanning,
            shouldShowOnlyCurrentSeason,
            currentSettings.showAdult))
    }

    fun updateShowAdult(shouldShowAdult: Boolean) {
        val currentSettings: CalendarSettings = _calendarSettings.value ?: CalendarSettings()
        _calendarSettings.onNext(CalendarSettings(currentSettings.showOnlyWatchingAndPlanning,
            currentSettings.showOnlyCurrentSeason,
            shouldShowAdult))
    }
}