package com.zen.alchan.ui.bio

import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.UserStatistics
import com.zen.alchan.helper.extensions.formatTwoDecimal
import com.zen.alchan.helper.pojo.BioItem
import com.zen.alchan.helper.pojo.Tendency
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import type.MediaListStatus

class BioViewModel : BaseViewModel() {

    private val _bioItems = BehaviorSubject.createDefault(listOf<BioItem>())

    val bioItems: Observable<List<BioItem>>
        get() = _bioItems

    override fun loadData() {
        // do nothing
    }

    fun getBioItems(profileData: ProfileData) {
        val bioItems = ArrayList<BioItem>()
        bioItems.add(BioItem(viewType = BioItem.VIEW_TYPE_ABOUT, bioText = profileData.user.about))

        val animeTendency = getTendency(profileData.user.statistics.anime)
        val mangaTendency = getTendency(profileData.user.statistics.manga)
        if (animeTendency != null || mangaTendency != null) {
            bioItems.add(
                BioItem(
                    viewType = BioItem.VIEW_TYPE_TENDENCY,
                    animeTendency = animeTendency,
                    mangaTendency = mangaTendency
                )
            )
        }
        _bioItems.onNext(bioItems)
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