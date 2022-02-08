package com.zen.alchan.ui.userstats

import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.UserStatisticTypes
import com.zen.alchan.data.response.anilist.UserStatisticsDetail
import com.zen.alchan.data.response.anilist.UserStatusStatistic
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.Statistic
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.helper.extensions.formatTwoDecimal
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
                            this.userId = user.id
                            emitStatsItems(user.statistics)
                        },
                        {
                            _error.onNext(it.getStringResource())
                        }
                    )
            )
        }
    }

    fun reloadData() {
        _loading.onNext(true)

        disposables.add(
            userRepository.getUserStatistics(userId, _sort.value ?: UserStatisticsSort.COUNT_DESC)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        emitStatsItems(it)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    private fun emitStatsItems(statisticTypes: UserStatisticTypes) {
        val stats = getCurrentStats(statisticTypes)

        val items = ArrayList<UserStatsItem>()
        val charts = ArrayList<Chart>()
        var countTotal = 0
        var durationTotal = 0
        var chapterTotal = 0

        stats.forEach {
            countTotal += it.count
            durationTotal += it.minutesWatched
            chapterTotal += it.chaptersRead

            val color = getChartColor(it)
            val label = getChartLabel(it)

            items.add(
                UserStatsItem(
                    stats = it,
                    color = color,
                    label = label,
                    mediaType = _mediaType.value ?: MediaType.ANIME,
                    viewType = UserStatsItem.VIEW_TYPE_INFO
                )
            )

            charts.add(
                Chart(
                    color = color,
                    label = label,
                    value = getChartValue(it)
                )
            )
        }

        items.forEach {
            val countPercentage = if (countTotal != 0) {
                ((it.stats?.count?.toDouble() ?: 0.0) / countTotal.toDouble() * 100).formatTwoDecimal()
            } else {
                "0.00"
            }

            val durationPercentage = if (durationTotal != 0) {
                ((it.stats?.minutesWatched?.toDouble() ?: 0.0) / durationTotal.toDouble() * 100).formatTwoDecimal()
            } else if (chapterTotal != 0) {
                ((it.stats?.chaptersRead?.toDouble() ?: 0.0) / chapterTotal.toDouble() * 100).formatTwoDecimal()
            } else {
                "0.00"
            }

            it.countPercentage = "(${countPercentage}%)"
            it.durationPercentage = "(${durationPercentage}%)"
        }

        if (shouldShowChart()) {
            items.add(0, UserStatsItem(charts, viewType = getChartType()))
        }

        _statsItems.onNext(items)
    }

    private fun getCurrentStats(statisticTypes: UserStatisticTypes): List<UserStatisticsDetail> {
        val userStatistics = when (_mediaType.value) {
            MediaType.ANIME -> statisticTypes.anime
            MediaType.MANGA -> statisticTypes.manga
            else -> statisticTypes.anime
        }

        return when (_statistic.value) {
            Statistic.STATUS -> userStatistics.statuses
            Statistic.FORMAT -> userStatistics.formats
            Statistic.SCORE -> userStatistics.scores
            Statistic.LENGTH -> userStatistics.lengths
            Statistic.RELEASE_YEAR -> userStatistics.releaseYears
            Statistic.START_YEAR -> userStatistics.startYears
            Statistic.GENRE -> userStatistics.genres
            Statistic.TAG -> userStatistics.tags
            Statistic.COUNTRY -> userStatistics.countries
            Statistic.VOICE_ACTOR -> userStatistics.voiceActors
            Statistic.STAFF -> userStatistics.staffs
            Statistic.STUDIO -> userStatistics.studios
            else -> userStatistics.statuses
        }
    }

    private fun shouldShowChart(): Boolean {
        return when (_statistic.value) {
            Statistic.STATUS -> true
            Statistic.FORMAT -> true
            Statistic.SCORE -> true
            Statistic.LENGTH -> true
            Statistic.RELEASE_YEAR -> true
            Statistic.START_YEAR -> true
            Statistic.GENRE -> false
            Statistic.TAG -> false
            Statistic.COUNTRY -> true
            Statistic.VOICE_ACTOR -> false
            Statistic.STAFF -> false
            Statistic.STUDIO -> false
            else -> true
        }
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
            is UserStatusStatistic -> userStatisticsDetail.status?.name?.convertFromSnakeCase(true) ?: ""
            else -> ""
        }
    }

    private fun getChartValue(userStatisticsDetail: UserStatisticsDetail): Double {
        return when (_sort.value) {
            UserStatisticsSort.COUNT_DESC -> userStatisticsDetail.count.toDouble()
            UserStatisticsSort.PROGRESS_DESC -> {
                when (_mediaType.value) {
                    MediaType.ANIME -> userStatisticsDetail.minutesWatched.toDouble()
                    MediaType.MANGA -> userStatisticsDetail.chaptersRead.toDouble()
                    else -> 0.0
                }
            }
            UserStatisticsSort.MEAN_SCORE_DESC -> userStatisticsDetail.meanScore
            else -> 0.0
        }
    }
}