package com.zen.alchan.ui.settings.list

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.ScoreFormat

class ListSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _scoringSystem = BehaviorSubject.createDefault(ScoreFormat.POINT_100)
    val scoringSystem: Observable<ScoreFormat>
        get() = _scoringSystem

    private val _useAdvancedScoring = BehaviorSubject.createDefault(false)
    val useAdvancedScoring: Observable<Boolean>
        get() = _useAdvancedScoring

    private val _advancedScoringCriteria = BehaviorSubject.createDefault<List<String>>(listOf())
    val advancedScoringCriteria: Observable<List<String>>
        get() = _advancedScoringCriteria

    private val _scoreFormats = PublishSubject.create<List<ScoreFormat>>()
    val scoreFormats: Observable<List<ScoreFormat>>
        get() = _scoreFormats

    private val _useAdvancedScoringVisibility = BehaviorSubject.createDefault(false)
    val useAdvancedScoringVisibility: Observable<Boolean>
        get() = _useAdvancedScoringVisibility

    private val _advancedScoringCriteriaVisibility = BehaviorSubject.createDefault(false)
    val advancedScoringCriteriaVisibility: Observable<Boolean>
        get() = _advancedScoringCriteriaVisibility

    private var viewer: User? = null
    private var currentListsSettings: MediaListOptions? = null

    override fun loadData() {
        disposables.add(
            userRepository.getViewer(Source.CACHE)
                .applyScheduler()
                .subscribe {
                    viewer = it

                    val mediaListOptions = it.mediaListOptions
                    currentListsSettings = mediaListOptions

                    updateScoringSystem(mediaListOptions.scoreFormat ?: ScoreFormat.POINT_100)
                    updateUseAdvancedScoring(mediaListOptions.animeList.advancedScoringEnabled)
                    updateAdvancedScoringCriteria(mediaListOptions.animeList.advancedScoring)
                }
        )
    }

    fun updateScoringSystem(newScoringSystem: ScoreFormat) {
        currentListsSettings?.scoreFormat = newScoringSystem
        _scoringSystem.onNext(newScoringSystem)

        when (newScoringSystem) {
            ScoreFormat.POINT_100, ScoreFormat.POINT_10_DECIMAL -> {
                _useAdvancedScoringVisibility.onNext(true)
            }
            else -> {
                _useAdvancedScoringVisibility.onNext(false)
                _useAdvancedScoring.onNext(false)
            }
        }

        handleAdvancedScoringCriteriaVisibility()
    }

    fun updateUseAdvancedScoring(shouldUseAdvancedScoring: Boolean) {
        currentListsSettings?.animeList?.advancedScoringEnabled = shouldUseAdvancedScoring
        currentListsSettings?.mangaList?.advancedScoringEnabled = shouldUseAdvancedScoring
        _useAdvancedScoring.onNext(shouldUseAdvancedScoring)

        handleAdvancedScoringCriteriaVisibility()
    }

    fun updateAdvancedScoringCriteria(newAdvancedScoringCriteria: List<String>) {
        _advancedScoringCriteria.onNext(newAdvancedScoringCriteria)
    }

    fun getScoreFormats() {
        _scoreFormats.onNext(ScoreFormat.values().toList().filterNot { it == ScoreFormat.UNKNOWN__ })
    }

    private fun handleAdvancedScoringCriteriaVisibility() {
        _advancedScoringCriteriaVisibility.onNext(
            (_scoringSystem.value == ScoreFormat.POINT_100 || _scoringSystem.value == ScoreFormat.POINT_10_DECIMAL) &&
            _useAdvancedScoring.value == true
        )
    }
}