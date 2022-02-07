package com.zen.alchan.ui.userstats

import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.UserStatisticsDetail
import com.zen.alchan.data.response.anilist.UserStatusStatistic
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.Statistic
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.Chart
import com.zen.alchan.helper.pojo.UserStatsItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import type.UserStatisticsSort

class UserStatsViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel() {

    private val _mediaType = BehaviorSubject.createDefault(MediaType.ANIME)
    val mediaType: Observable<MediaType>
        get() = _mediaType

    private val _sort = BehaviorSubject.createDefault(UserStatisticsSort.COUNT_DESC)
    val sort: Observable<UserStatisticsSort>
        get() = _sort

    private val _statistic = BehaviorSubject.createDefault(Statistic.STATUS)
    val statistic: Observable<Statistic>
        get() = _statistic

    private val _statsItems = BehaviorSubject.createDefault(listOf<UserStatsItem>())
    val statsItems: Observable<List<UserStatsItem>>
        get() = _statsItems

    private var userId: Int = 0

    override fun loadData() {
        // do nothing
    }

    fun loadData(userId: Int) {
        loadOnce {
            this.userId = userId

            val userObservable = if (userId == 0) {
                userRepository.getViewer(Source.CACHE)
            } else {
                browseRepository.getUser(userId)
            }

            _loading.onNext(true)

            disposables.add(
                userObservable
                    .applyScheduler()
                    .doFinally {
                        _loading.onNext(false)
                    }
                    .subscribe(
                        { user ->
                            emitStatsItems(user.statistics.anime.statuses)
                        },
                        {
                            _error.onNext(it.getStringResource())
                        }
                    )
            )
        }
    }

    private fun emitStatsItems(stats: List<UserStatisticsDetail>) {
        val items = ArrayList<UserStatsItem>()
        val charts = ArrayList<Chart>()
        stats.forEach {
            items.add(
                UserStatsItem(
                    stats = it,
                    viewType = UserStatsItem.VIEW_TYPE_INFO
                )
            )

            if (shouldShowChart()) {
                charts.add(
                    Chart(
                        color = getChartColor(it),
                        label = getChartLabel(it),
                        value = it.count.toDouble()
                    )
                )
            }
        }

        if (charts.isNotEmpty()) {
            items.add(0, UserStatsItem(charts, null, getChartType()))
        }

        _statsItems.onNext(items)
    }

    private fun shouldShowChart(): Boolean {
        return _statistic.value?.showChart ?: false
    }

    private fun getChartType(): Int {
        return when (_statistic.value) {
            Statistic.STATUS, Statistic.FORMAT, Statistic.COUNTRY -> UserStatsItem.VIEW_TYPE_PIE_CHART
            Statistic.SCORE, Statistic.LENGTH -> UserStatsItem.VIEW_TYPE_BAR_CHART
            Statistic.RELEASE_YEAR, Statistic.START_YEAR -> UserStatsItem.VIEW_TYPE_LINE_CHART
            else -> 0
        }
    }

    private fun getChartColor(userStatisticsDetail: UserStatisticsDetail): String {
        return "#000000"
    }

    private fun getChartLabel(userStatisticsDetail: UserStatisticsDetail): String {
        return when (userStatisticsDetail) {
            is UserStatusStatistic -> userStatisticsDetail.status?.name?.convertFromSnakeCase(false) ?: ""
            else -> ""
        }
    }
}