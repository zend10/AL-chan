package com.zen.alchan.ui.profile

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserStatistics
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.formatTwoDecimal
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.helper.pojo.BioItem
import com.zen.alchan.helper.pojo.Tendency
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import type.MediaListStatus

class SharedProfileViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val userDataSubject = BehaviorSubject.createDefault(User.EMPTY_USER)
    private val bioItemsSubject = BehaviorSubject.createDefault(listOf<BioItem>())
    private val animeCountSubject = BehaviorSubject.createDefault(0)
    private val mangaCountSubject = BehaviorSubject.createDefault(0)
    private val followingCountSubject = BehaviorSubject.createDefault(0)
    private val followersCountSubject = BehaviorSubject.createDefault(0)

    val userData: Observable<User>
        get() = userDataSubject

    val bioItems: Observable<List<BioItem>>
        get() = bioItemsSubject

    val animeCount: Observable<Int>
        get() = animeCountSubject

    val mangaCount: Observable<Int>
        get() = mangaCountSubject

    val followingCount: Observable<Int>
        get() = followingCountSubject

    val followersCount: Observable<Int>
        get() = followersCountSubject

    var userId = 0

    fun checkIsAuthenticated() {
        disposables.add(
            authenticationRepository.getIsAuthenticated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    loadUserData()
                    isAuthenticatedSubject.onNext(it)
                }
        )
    }

    private fun loadUserData(isReloading: Boolean = false) {
        if (!isReloading && state == State.LOADED) return

        disposables.add(
            authenticationRepository.viewer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    userId = it.id
                    loadProfileData(if (isReloading) Source.NETWORK else null)

                    userDataSubject.onNext(it)

                    state = State.LOADED
                }
        )

        // TODO: update this to be able to get user data of other user as well
        authenticationRepository.getViewerData()
    }

    private fun loadProfileData(source: Source?) {
        disposables.add(
            userRepository.getProfileData(userId, source = source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val bioItems = ArrayList<BioItem>()
                        bioItems.add(BioItem(viewType = BioItem.VIEW_TYPE_ABOUT, bioText = it.user.about))

                        val animeTendency = getTendency(it.user.statistics.anime)
                        val mangaTendency = getTendency(it.user.statistics.manga)
                        if (animeTendency != null || mangaTendency != null) {
                            bioItems.add(
                                BioItem(
                                    viewType = BioItem.VIEW_TYPE_TENDENCY,
                                    animeTendency = animeTendency,
                                    mangaTendency = mangaTendency
                                )
                            )
                        }
                        bioItemsSubject.onNext(bioItems)

                        animeCountSubject.onNext(
                            it.user.statistics.anime.statuses.find { anime ->
                                anime.status == MediaListStatus.COMPLETED
                            }?.count ?: 0
                        )
                        mangaCountSubject.onNext(
                            it.user.statistics.manga.statuses.find { manga ->
                                manga.status == MediaListStatus.COMPLETED
                            }?.count ?: 0
                        )
                        followingCountSubject.onNext(
                            it.following.pageInfo.total
                        )
                        followersCountSubject.onNext(
                            it.followers.pageInfo.total
                        )
                    },
                    {
                        errorSubject.onNext(it.sendMessage())
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

    enum class Page {
        BIO,
        FAVORITE,
        STATS,
        ACTIVITY
    }

    companion object {
        private const val TENDENCY_FAVORITES_COUNT = 3
        private const val TENDENCY_FAVORITES_SEPARATOR = "/"
        private const val TENDENCY_MINIMUM_COMPLETED = 20
    }
}