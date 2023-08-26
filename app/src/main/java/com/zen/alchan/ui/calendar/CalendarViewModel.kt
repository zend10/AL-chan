package com.zen.alchan.ui.calendar

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.CalendarSetting
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

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private val _airingSchedules = BehaviorSubject.createDefault<List<AiringSchedule>>(listOf())
    val airingSchedules: Observable<List<AiringSchedule>>
        get() = _airingSchedules

    private val _date = BehaviorSubject.createDefault(TimeUtil.getTodayInSeconds())
    val date: Observable<Int>
        get() = _date

    private val _showOnlyWatchingAndPlanning = BehaviorSubject.createDefault(true)
    val showOnlyWatchingAndPlanning: Observable<Boolean>
        get() = _showOnlyWatchingAndPlanning

    private val _showOnlyCurrentSeason = BehaviorSubject.createDefault(false)
    val showOnlyCurrentSeason: Observable<Boolean>
        get() = _showOnlyCurrentSeason

    private val _showAdult = BehaviorSubject.createDefault(false)
    val showAdult: Observable<Boolean>
        get() = _showAdult

    private val _calendarDate = PublishSubject.create<Calendar>()
    val calendarDate: Observable<Calendar>
        get() = _calendarDate

    private val previousPagesAiringSchedules = ArrayList<AiringSchedule>()
    private var allAiringSchedules = ArrayList<AiringSchedule>()

    private var calendarSetting = CalendarSetting()

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                Observable.zip(
                    userRepository.getAppSetting(),
                    userRepository.getCalendarSetting()
                ) { appSetting, calendarSetting ->
                    appSetting to calendarSetting
                }
                    .applyScheduler()
                    .subscribe { (appSetting, calendarSetting) ->
                        _appSetting.onNext(appSetting)

                        this.calendarSetting = calendarSetting
                        _showOnlyWatchingAndPlanning.onNext(calendarSetting.showOnlyWatchingAndPlanning)
                        _showOnlyCurrentSeason.onNext(calendarSetting.showOnlyCurrentSeason)
                        _showAdult.onNext(calendarSetting.showAdult)

                        loadCalendar()
                    }
            )
        }
    }

    fun reloadCalendar() {
        loadCalendar()
    }

    private fun loadCalendar(page: Int = 1) {
        _loading.onNext(true)
        state = State.LOADING

        val currentDateInSeconds = _date.value ?: TimeUtil.getTodayInSeconds()
        disposables.add(
            contentRepository.getAiringSchedule(page, currentDateInSeconds,  currentDateInSeconds + (3600 * 24))
                .applyScheduler()
                .doFinally {
                    if (state != State.LOADING)
                        _emptyLayoutVisibility.onNext(_airingSchedules.value.isNullOrEmpty())
                }
                .subscribe(
                    {
                        if (it.pageInfo.hasNextPage) {
                            previousPagesAiringSchedules.addAll(it.data)
                            loadCalendar(page + 1)
                        } else {
                            previousPagesAiringSchedules.addAll(it.data)
                            allAiringSchedules = ArrayList(
                                previousPagesAiringSchedules.map {
                                    val streamingOnlyLinks = it.media.externalLinks.filter { it.type == ExternalLinkType.STREAMING }
                                    it.copy(media = it.media.copy(externalLinks = streamingOnlyLinks))
                                }
                            )
                            val items = getFilteredAiringSchedules()
                            _airingSchedules.onNext(items)
                            state = State.LOADED
                            previousPagesAiringSchedules.clear()
                            _loading.onNext(false)
                        }
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                        previousPagesAiringSchedules.clear()
                        _loading.onNext(false)
                    }
                )
        )
    }

    fun loadDateCalendar() {
        val dateInSeconds = _date.value ?: TimeUtil.getTodayInSeconds()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInSeconds * 1000L
        _calendarDate.onNext(calendar)
    }

    fun updateDate(date: Int, month: Int, year: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, date)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dateInSeconds = (calendar.timeInMillis / 1000L).toInt()
        _date.onNext(dateInSeconds)
        reloadCalendar()
    }

    fun updateShowOnlyWatchingAndPlanning(shouldShowOnlyWatchingAndPlanning: Boolean) {
        calendarSetting.showOnlyWatchingAndPlanning = shouldShowOnlyWatchingAndPlanning
        saveCalendarSetting()

        _showOnlyWatchingAndPlanning.onNext(shouldShowOnlyWatchingAndPlanning)
        val filteredAiringSchedules = getFilteredAiringSchedules()
        _airingSchedules.onNext(filteredAiringSchedules)
        _emptyLayoutVisibility.onNext(filteredAiringSchedules.isEmpty())
    }

    fun updateShowOnlyCurrentSeason(shouldShowOnlyCurrentSeason: Boolean) {
        calendarSetting.showOnlyCurrentSeason = shouldShowOnlyCurrentSeason
        saveCalendarSetting()

        _showOnlyCurrentSeason.onNext(shouldShowOnlyCurrentSeason)
        val filteredAiringSchedules = getFilteredAiringSchedules()
        _airingSchedules.onNext(filteredAiringSchedules)
        _emptyLayoutVisibility.onNext(filteredAiringSchedules.isEmpty())
    }

    fun updateShowAdult(shouldShowAdult: Boolean) {
        calendarSetting.showAdult = shouldShowAdult
        saveCalendarSetting()

        _showAdult.onNext(shouldShowAdult)
        val filteredAiringSchedules = getFilteredAiringSchedules()
        _airingSchedules.onNext(filteredAiringSchedules)
        _emptyLayoutVisibility.onNext(filteredAiringSchedules.isEmpty())
    }

    private fun saveCalendarSetting() {
        disposables.add(
            userRepository.setCalendarSetting(calendarSetting)
                .applyScheduler()
                .subscribe(
                    {
                        // do nothing
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    private fun getFilteredAiringSchedules(): List<AiringSchedule> {
        val showOnlyWatchingAndPlanning = _showOnlyWatchingAndPlanning.value ?: true
        val showOnlyCurrentSeason = _showOnlyCurrentSeason.value ?: false
        val showAdult = _showAdult.value ?: false
        val items = allAiringSchedules
            .filter {
                if (showOnlyWatchingAndPlanning && !(it.media.mediaListEntry?.status == MediaListStatus.CURRENT || it.media.mediaListEntry?.status == MediaListStatus.PLANNING)) {
                    return@filter false
                }

                if (!showAdult && it.media.isAdult) {
                    return@filter false
                }

                if (showOnlyCurrentSeason && !(it.media.season == TimeUtil.getCurrentSeason() && it.media.seasonYear == TimeUtil.getCurrentYear())) {
                    return@filter false
                }

                return@filter true
            }
        return items
    }
}