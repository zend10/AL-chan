package com.zen.alchan.ui.userstats

import android.graphics.Color
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.Chart
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.UserStatsItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.zen.alchan.type.MediaFormat
import com.zen.alchan.type.MediaListStatus
import com.zen.alchan.type.UserStatisticsSort
import kotlin.math.roundToInt

class UserStatsViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel<UserStatsParam>() {

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

    private val animeLengths = arrayListOf("1", "2-6", "7-16", "17-28", "29-55", "56-100", "101+", "")
    private val mangaLengths = arrayListOf("1", "2-10", "11-25", "26-50", "51-100", "101-200", "201+", "")
    private val scoreList = arrayListOf(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)

    override fun loadData(param: UserStatsParam) {
        loadOnce {
            userId = param.userId

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
                    showRank = _statistic.value == Statistic.GENRE || _statistic.value == Statistic.TAG || _statistic.value == Statistic.VOICE_ACTOR || _statistic.value == Statistic.STAFF || _statistic.value == Statistic.STUDIO,
                    isClickable = _statistic.value == Statistic.VOICE_ACTOR || _statistic.value == Statistic.STAFF || _statistic.value == Statistic.STUDIO,
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
            MediaType.ANIME -> animeLengths
            MediaType.MANGA -> mangaLengths
            else -> animeLengths
        }

        return when (_statistic.value) {
            Statistic.STATUS -> userStatistics.statuses
            Statistic.FORMAT -> userStatistics.formats
            Statistic.SCORE -> scoreList.map { score ->
                userStatistics.scores.find { it.meanScore.roundToInt() == score } ?: UserScoreStatistic(meanScore = score.toDouble(), score = score)
            }
            Statistic.LENGTH -> lengthList.map { length ->
                userStatistics.lengths.find { it.length == length } ?: UserLengthStatistic(length = length)
            }
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
        if (_statistic.value == Statistic.SCORE || _statistic.value == Statistic.LENGTH || _statistic.value == Statistic.RELEASE_YEAR || _statistic.value == Statistic.START_YEAR)
            return true

        if (_sort.value?.first == UserStatisticsSort.MEAN_SCORE_DESC)
            return false

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
        return when (userStatisticsDetail) {
            is UserStatusStatistic -> {
                userStatisticsDetail.status?.getColor()
            }
            is UserFormatStatistic -> {
                when (userStatisticsDetail.format) {
                    MediaFormat.TV -> "#55e2cf"
                    MediaFormat.TV_SHORT -> "#57aee2"
                    MediaFormat.MOVIE -> "#5668e2"
                    MediaFormat.SPECIAL -> "#8a56e2"
                    MediaFormat.OVA -> "#ce56e2"
                    MediaFormat.ONA -> "#e256ae"
                    MediaFormat.MUSIC -> "#e25768"
                    MediaFormat.MANGA -> "#aee256"
                    MediaFormat.NOVEL -> "#68e257"
                    MediaFormat.ONE_SHOT -> "#56e28a"
                    else -> null
                }
            }
            is UserCountryStatistic -> {
                when (userStatisticsDetail.country) {
                    Country.JAPAN.iso -> "#BC002D"
                    Country.SOUTH_KOREA.iso -> "#32569D"
                    Country.CHINA.iso -> "#F9B60B"
                    Country.JAPAN.iso -> "#000095"
                    else -> null
                }
            }
            is UserScoreStatistic -> {
                when (userStatisticsDetail.meanScore.roundToInt()) {
                    10 -> "#d2492d"
                    20 -> "#d2642c"
                    30 -> "#d2802e"
                    40 -> "#d29d2f"
                    50 -> "#d2b72e"
                    60 -> "#d3d22e"
                    70 -> "#b8d22c"
                    80 -> "#9cd42e"
                    90 -> "#81d12d"
                    100 -> "#63d42e"
                    else -> null
                }
            }
            is UserLengthStatistic -> {
                when (userStatisticsDetail.length) {
                    animeLengths[0], mangaLengths[0] -> "#d2492d"
                    animeLengths[1], mangaLengths[1] -> "#d2642c"
                    animeLengths[2], mangaLengths[2] -> "#d2802e"
                    animeLengths[3], mangaLengths[3] -> "#d29d2f"
                    animeLengths[4], mangaLengths[4] -> "#d2b72e"
                    animeLengths[5], mangaLengths[5] -> "#d3d22e"
                    animeLengths[6], mangaLengths[6] -> "#b8d22c"
                    animeLengths[7], mangaLengths[7] -> "#9cd42e"
                    else -> null
                }
            }
            else -> null
        }
    }

    private fun getChartLabel(userStatisticsDetail: UserStatisticsDetail): String {
        return when (userStatisticsDetail) {
            is UserStatusStatistic -> userStatisticsDetail.status?.name?.convertFromSnakeCase(true) ?: ""
            is UserFormatStatistic -> userStatisticsDetail.format?.name?.convertFromSnakeCase(true) ?: ""
            is UserScoreStatistic -> userStatisticsDetail.meanScore.roundToInt().toString()
            is UserLengthStatistic -> if (userStatisticsDetail.length.isNotBlank()) userStatisticsDetail.length else "?"
            is UserReleaseYearStatistic -> userStatisticsDetail.releaseYear.toString()
            is UserStartYearStatistic -> userStatisticsDetail.startYear.toString()
            is UserGenreStatistic -> userStatisticsDetail.genre
            is UserTagStatistic -> userStatisticsDetail.tag?.name ?: ""
            is UserCountryStatistic -> Country.values().firstOrNull { it.iso == userStatisticsDetail.country }?.getString() ?: ""
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