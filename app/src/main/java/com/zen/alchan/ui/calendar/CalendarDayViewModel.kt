package com.zen.alchan.ui.calendar

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.AiringSchedule
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.type.ExternalLinkType
import com.zen.alchan.type.MediaListStatus
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class CalendarDayViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : BaseViewModel<CalendarDayParam>() {

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    private val _airingSchedules = BehaviorSubject.createDefault<List<AiringSchedule>>(listOf())
    val airingSchedules: Observable<List<AiringSchedule>>
        get() = _airingSchedules

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private val _calendarSettings = BehaviorSubject.create<CalendarSettings>()
    val calendarSettings: Observable<CalendarSettings>
        get() = _calendarSettings


    private val previousPagesAiringSchedules = ArrayList<AiringSchedule>()
    private var allAiringSchedules = ArrayList<AiringSchedule>()

    override fun loadData(param: CalendarDayParam) {
        loadOnce {
            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _appSetting.onNext(it)
                        loadCalendar(param.date)
                    }
            )
        }
    }

    fun updateCalendarSettings(settings: CalendarSettings) {
        _calendarSettings.onNext(settings)
        val filteredAiringSchedules = getFilteredAiringSchedules()
        _airingSchedules.onNext(filteredAiringSchedules)
        _emptyLayoutVisibility.onNext(filteredAiringSchedules.isEmpty())
    }

    private fun loadCalendar(date: Int, page: Int = 1) {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            contentRepository.getAiringSchedule(page, date, date + (3600 * 24))
                .applyScheduler()
                .doFinally {
                    if (state != State.LOADING)
                        _emptyLayoutVisibility.onNext(_airingSchedules.value.isNullOrEmpty())
                }
                .subscribe({
                    previousPagesAiringSchedules.addAll(it.data)

                    if (it.pageInfo.hasNextPage) {
                        loadCalendar(date, page + 1)
                    } else {
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

                }, {
                    _error.onNext(it.getStringResource())
                    state = State.ERROR
                    previousPagesAiringSchedules.clear()
                    _loading.onNext(false)
                })
        )
    }

    private fun getFilteredAiringSchedules(): List<AiringSchedule> {
        val showOnlyWatchingAndPlanning = _calendarSettings.value?.showOnlyWatchingAndPlanning ?: true
        val showOnlyCurrentSeason = _calendarSettings.value?.showOnlyCurrentSeason ?: false
        val showAdult = _calendarSettings.value?.showAdult ?: false
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