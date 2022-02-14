package com.zen.alchan.ui.userstats

import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.Statistic
import com.zen.alchan.helper.enums.getStringResource
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.helper.extensions.formatTwoDecimal
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.Chart
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.UserStatsItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.UserStatisticsSort

class UserStatsViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel() {

    private val _mediaType = BehaviorSubject.createDefault(MediaType.ANIME)
    val mediaType: Observable<MediaType>
        get() = _mediaType

    private val _sort = BehaviorSubject.createDefault(UserStatisticsSort.COUNT_DESC to MediaType.ANIME)
    val sort: Observable<Pair<UserStatisticsSort, MediaType>>
        get() = _sort

    private val _statistic = BehaviorSubject.createDefault(Statistic.STATUS)
    val statistic: Observable<Statistic>
        get() = _statistic

    private val _statsItems = BehaviorSubject.createDefault(listOf<UserStatsItem>())
    val statsItems: Observable<List<UserStatsItem>>
        get() = _statsItems

    private val _statistics = PublishSubject.create<List<ListItem<Statistic>>>()
    val statistics: Observable<List<ListItem<Statistic>>>
        get() = _statistics

    private val _mediaTypes = PublishSubject.create<List<ListItem<MediaType>>>()
    val mediaTypes: Observable<List<ListItem<MediaType>>>
        get() = _mediaTypes

    private val _sorts = PublishSubject.create<List<ListItem<UserStatisticsSort>>>()
    val sorts: Observable<List<ListItem<UserStatisticsSort>>>
        get() = _sorts

    private val _mediaTypeVisibility = BehaviorSubject.createDefault(true)
    val mediaTypeVisibility: Observable<Boolean>
        get() = _mediaTypeVisibility

    private val _sortVisibility = BehaviorSubject.createDefault(true)
    val sortVisibility: Observable<Boolean>
        get() = _sortVisibility

    private var userId: Int = 0
    private var userStatisticTypes = UserStatisticTypes()

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
                            userStatisticTypes = user.statistics
                            emitStatsItems()
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
            userRepository.getUserStatistics(userId, _sort.value?.first ?: UserStatisticsSort.COUNT_DESC)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        userStatisticTypes = it
                        emitStatsItems()
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun loadStatistics() {
        _statistics.onNext(Statistic.values().map { ListItem(it.getStringResource(), it) })
    }

    fun loadMediaTypes() {
        _mediaTypes.onNext(MediaType.values().map { ListItem(it.getStringResource(), it) })
    }

    fun loadSorts() {
        val sorts = listOf(UserStatisticsSort.COUNT_DESC, UserStatisticsSort.PROGRESS_DESC, UserStatisticsSort.MEAN_SCORE_DESC)
        _sorts.onNext(sorts.map { ListItem(it.getStringResource(_mediaType.value ?: MediaType.ANIME), it) })
    }

    fun updateStatistic(newStatistic: Statistic) {
        _statistic.onNext(newStatistic)
        emitStatsItems()

        _mediaTypeVisibility.onNext(newStatistic != Statistic.VOICE_ACTOR && newStatistic != Statistic.STUDIO)
        _sortVisibility.onNext(newStatistic != Statistic.SCORE && newStatistic != Statistic.LENGTH && newStatistic != Statistic.RELEASE_YEAR && newStatistic != Statistic.START_YEAR)
    }

    fun updateMediaType(newMediaType: MediaType) {
        _mediaType.onNext(newMediaType)
        emitStatsItems()
    }

    fun updateSort(newSort: UserStatisticsSort) {
        _sort.onNext(newSort to (_mediaType.value ?: MediaType.ANIME))
        reloadData()
    }

    private fun emitStatsItems() {
        val stats = getCurrentStats(userStatisticTypes)

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
        if (_statistic.value == Statistic.VOICE_ACTOR || _statistic.value == Statistic.STUDIO) {
            _mediaType.onNext(MediaType.ANIME)
        }

        val userStatistics = when (_mediaType.value) {
            MediaType.ANIME -> statisticTypes.anime
            MediaType.MANGA -> statisticTypes.manga
            else -> statisticTypes.anime
        }

        val lengthList = when (_mediaType.value) {
            MediaType.ANIME -> arrayListOf("1", "2-6", "7-16", "17-28", "29-55", "56-100", "101+", "")
            MediaType.MANGA -> arrayListOf("1", "2-10", "11-25", "26-50", "51-100", "101-200", "201+", "")
            else -> arrayListOf("1", "2-6", "7-16", "17-28", "29-55", "56-100", "101+", "Unknown")
        }

        return when (_statistic.value) {
            Statistic.STATUS -> userStatistics.statuses
            Statistic.FORMAT -> userStatistics.formats
            Statistic.SCORE -> userStatistics.scores.sortedBy { it.score }
            Statistic.LENGTH -> userStatistics.lengths.sortedBy { lengthList.indexOf(it.length) }
            Statistic.RELEASE_YEAR -> userStatistics.releaseYears.sortedBy { it.releaseYear }
            Statistic.START_YEAR -> userStatistics.startYears.sortedBy { it.startYear }
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

    private fun getChartColor(userStatisticsDetail: UserStatisticsDetail): String? {
        return null
    }

    private fun getChartLabel(userStatisticsDetail: UserStatisticsDetail): String {
        return when (userStatisticsDetail) {
            is UserStatusStatistic -> userStatisticsDetail.status?.name?.convertFromSnakeCase(true) ?: ""
            is UserFormatStatistic -> userStatisticsDetail.format?.name?.convertFromSnakeCase(true) ?: ""
            is UserScoreStatistic -> userStatisticsDetail.score.toString()
            is UserLengthStatistic -> if (userStatisticsDetail.length.isNotBlank()) userStatisticsDetail.length else "Unknown"
            is UserReleaseYearStatistic -> userStatisticsDetail.releaseYear.toString()
            is UserStartYearStatistic -> userStatisticsDetail.startYear.toString()
            is UserGenreStatistic -> userStatisticsDetail.genre
            is UserTagStatistic -> userStatisticsDetail.tag?.name ?: ""
            is UserCountryStatistic -> userStatisticsDetail.country
            is UserVoiceActorStatistic -> userStatisticsDetail.voiceActor?.name?.userPreferred ?: ""
            is UserStaffStatistic -> userStatisticsDetail.staff?.name?.userPreferred ?: ""
            is UserStudioStatistic -> userStatisticsDetail.studio?.name ?: ""
            else -> ""
        }
    }

    private fun getChartValue(userStatisticsDetail: UserStatisticsDetail): Double {
        return when (_sort.value?.first) {
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