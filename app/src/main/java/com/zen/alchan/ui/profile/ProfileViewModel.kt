package com.zen.alchan.ui.profile

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserStatistics
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.formatTwoDecimal
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ProfileItem
import com.zen.alchan.helper.pojo.Tendency
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import type.MediaListStatus

class ProfileViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _viewerMenutItemVisibility = BehaviorSubject.createDefault(false)
    val viewerMenutItemVisibility: Observable<Boolean>
        get() = _viewerMenutItemVisibility

    private val _bestFriendVisibility = BehaviorSubject.createDefault(false)
    val bestFriendVisibility: Observable<Boolean>
        get() = _bestFriendVisibility

    private val _notLoggedInLayoutVisibility = BehaviorSubject.createDefault(false)
    val notLoggedInLayoutVisibility: Observable<Boolean>
        get() = _notLoggedInLayoutVisibility

    private val _avatarUrl = BehaviorSubject.createDefault("" to false)
    val avatarUrl: Observable<Pair<String, Boolean>>
        get() = _avatarUrl

    private val _bannerUrl = BehaviorSubject.createDefault("")
    val bannerUrl: Observable<String>
        get() = _bannerUrl

    private val _username = BehaviorSubject.createDefault("")
    val username: Observable<String>
        get() = _username

    private val _animeCompletedCount = BehaviorSubject.createDefault(0)
    val animeCompletedCount: Observable<Int>
        get() = _animeCompletedCount

    private val _mangaCompletedCount = BehaviorSubject.createDefault(0)
    val mangaCompletedCount: Observable<Int>
        get() = _mangaCompletedCount

    private val _followingCount = BehaviorSubject.createDefault(0)
    val followingCount: Observable<Int>
        get() = _followingCount

    private val _followersCount = BehaviorSubject.createDefault(0)
    val followersCount: Observable<Int>
        get() = _followersCount

    private val _profileItemList = BehaviorSubject.createDefault(listOf<ProfileItem>())
    val profileItemList: Observable<List<ProfileItem>>
        get() = _profileItemList

    private var userId = 0
    private var user = User()
    private var appSetting = AppSetting()

    override fun loadData() {
        // do nothing
    }

    fun loadData(userId: Int) {
        loadOnce {
            disposables.add(
                userRepository.getIsAuthenticated()
                    .applyScheduler()
                    .subscribe {
                        _notLoggedInLayoutVisibility.onNext(userId == 0 && !it)
                        _viewerMenutItemVisibility.onNext(userId == 0)

                        loadUserData()
                    }
            )

        }
    }

    fun logout() {
        userRepository.logoutAsGuest()
    }

    fun reloadData() {
        loadUserData(true)
    }

    private fun loadUserData(isReloading: Boolean = false) {
        _loading.onNext(true)

        if (userId == 0) {
            disposables.add(
                userRepository.getViewer()
                    .zipWith(userRepository.getAppSetting()) { user, appSetting ->
                        return@zipWith user to appSetting
                    }
                    .applyScheduler()
                    .subscribe(
                        { (user, appSetting) ->
                            this.user = user
                            this.appSetting = appSetting

                            userId = user.id

                            _avatarUrl.onNext(user.avatar.large to appSetting.useCircularAvatarForProfile)
                            _bannerUrl.onNext(user.bannerImage)
                            _username.onNext(user.name)

                            loadProfileData(if (isReloading) Source.NETWORK else null)
                        },
                        {
                            _loading.onNext(false)
                            _error.onNext(it.getStringResource())
                            state = State.ERROR
                        }
                    )
            )
        } else {
            // load user based on userId
        }
    }

    private fun loadProfileData(source: Source?) {
        disposables.add(
            userRepository.getProfileData(userId, source = source)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        _animeCompletedCount.onNext(
                            it.user.statistics.anime.statuses.find { anime ->
                                anime.status == MediaListStatus.COMPLETED
                            }?.count ?: 0
                        )
                        _mangaCompletedCount.onNext(
                            it.user.statistics.manga.statuses.find { manga ->
                                manga.status == MediaListStatus.COMPLETED
                            }?.count ?: 0
                        )
                        _followingCount.onNext(it.following.pageInfo.total)
                        _followersCount.onNext(it.followers.pageInfo.total)


                        val profileItemList = mutableListOf(
                            ProfileItem(user = user, viewType = ProfileItem.VIEW_TYPE_BIO),
                            ProfileItem(affinity = Pair(30.0, -23.45), viewType = ProfileItem.VIEW_TYPE_AFFINITY)
                        )

                        val animeTendency = getTendency(it.user.statistics.anime)
                        val mangaTendency = getTendency(it.user.statistics.manga)
                        if (animeTendency != null || mangaTendency != null)
                            profileItemList.add(ProfileItem(tendency = animeTendency to mangaTendency, viewType = ProfileItem.VIEW_TYPE_TENDENCY))

                        _profileItemList.onNext(profileItemList)

                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    private fun getTendency(statistics: UserStatistics): Tendency? {
        if (statistics.statuses.find { it.status == MediaListStatus.COMPLETED }?.count ?: 0 < TENDENCY_MINIMUM_COMPLETED)
            return null

        var mostFavoriteGenres = ""
        var leastFavoriteGenre = ""
        var mostFavoriteTags = ""
        var mostFavoriteYear = ""
        var startYear = ""
        var completedSeriesPercentage = ""

        val totalCount = statistics.statuses.filter { it.status != MediaListStatus.PLANNING }.sumBy { it.count }.toDouble()

        if (statistics.genres.isNotEmpty()) {
            val genreWeightedScores = statistics.genres.map {
                Pair(it.genre, calculateWeightedScore(it.count, totalCount, it.meanScore))
            }

            val sortedGenreWeightedScores = genreWeightedScores.sortedByDescending { it.second }

            mostFavoriteGenres = sortedGenreWeightedScores
                .take(TENDENCY_FAVORITES_COUNT)
                .joinToString(TENDENCY_FAVORITES_SEPARATOR) { it.first }

            if (sortedGenreWeightedScores.size > TENDENCY_FAVORITES_COUNT) {
                leastFavoriteGenre = sortedGenreWeightedScores.last().first
            }
        }

        if (statistics.tags.isNotEmpty()) {
            val tagWeightedScores = statistics.tags.filter { it.tag?.isAdult == false }.map {
                Pair(it.tag?.name ?: "", calculateWeightedScore(it.count, totalCount, it.meanScore))
            }

            mostFavoriteTags = tagWeightedScores
                .sortedByDescending { it.second }
                .take(TENDENCY_FAVORITES_COUNT)
                .joinToString(TENDENCY_FAVORITES_SEPARATOR) { it.first }
        }

        if (statistics.releaseYears.isNotEmpty()) {
            val yearWeightedScores = statistics.releaseYears.map {
                Pair(it.releaseYear.toString(), calculateWeightedScore(it.count, totalCount, it.meanScore))
            }

            mostFavoriteYear = yearWeightedScores
                .sortedByDescending { it.second }
                .take(TENDENCY_FAVORITES_COUNT)
                .joinToString(TENDENCY_FAVORITES_SEPARATOR) { it.first }
        }

        if (statistics.startYears.isNotEmpty()) {
            startYear = statistics.startYears.minOf { it.startYear }.toString()
        }

        if (statistics.statuses.isNotEmpty()) {
            val completedTotal = statistics.statuses.filter {
                it.status == MediaListStatus.COMPLETED || it.status == MediaListStatus.REPEATING
            }.sumBy { it.count }

            if (totalCount != 0.0) {
                val completedPercentage = completedTotal.toDouble() / totalCount
                completedSeriesPercentage = (completedPercentage * 100).formatTwoDecimal() + "%"
            }
        }

        return Tendency(
            mostFavoriteGenres,
            leastFavoriteGenre,
            mostFavoriteTags,
            mostFavoriteYear,
            startYear,
            completedSeriesPercentage
        )
    }

    private fun calculateWeightedScore(count: Int, totalCount: Double, meanScore: Double): Double {
        return count.toDouble() / totalCount + meanScore / 100.0
    }

    companion object {
        private const val TENDENCY_FAVORITES_COUNT = 3
        private const val TENDENCY_FAVORITES_SEPARATOR = "/"
        private const val TENDENCY_MINIMUM_COMPLETED = 20
    }
}