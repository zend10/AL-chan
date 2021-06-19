package com.zen.alchan.ui.settings.list

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.ListOrder
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

    private val _defaultListOrder = BehaviorSubject.createDefault(ListOrder.TITLE)
    val defaultListOrder: Observable<ListOrder>
        get() = _defaultListOrder

    private val _scoreFormats = PublishSubject.create<List<ScoreFormat>>()
    val scoreFormats: Observable<List<ScoreFormat>>
        get() = _scoreFormats

    private val _useAdvancedScoringVisibility = BehaviorSubject.createDefault(false)
    val useAdvancedScoringVisibility: Observable<Boolean>
        get() = _useAdvancedScoringVisibility

    private val _advancedScoringCriteriaVisibility = BehaviorSubject.createDefault(false)
    val advancedScoringCriteriaVisibility: Observable<Boolean>
        get() = _advancedScoringCriteriaVisibility

    private val _advancedScoringNoItemTextVisibility = BehaviorSubject.createDefault(false)
    val advancedScoringNoItemTextVisibility: Observable<Boolean>
        get() = _advancedScoringNoItemTextVisibility

    private val _listOrders = PublishSubject.create<List<ListOrder>>()
    val listOrders: Observable<List<ListOrder>>
        get() = _listOrders

    private var viewer: User? = null
    private var currentListsSettings: MediaListOptions? = null

    override fun loadData() {
        if (state == State.LOADED)
            return

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

                    try {
                        val listOrder = ListOrder.valueOf(mediaListOptions.rowOrder)
                        updateDefaultListOrder(listOrder)
                    } catch (e: IllegalArgumentException) {
                        updateDefaultListOrder(ListOrder.TITLE)
                    }

                    state = State.LOADED
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
        _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoringCriteria.isEmpty())
        _advancedScoringCriteria.onNext(newAdvancedScoringCriteria)
    }

    fun addAdvancedScoringCriteria(newAdvancedScoring: String) {
        val newAdvancedScoringCriteria = _advancedScoringCriteria.value?.toMutableList()
        newAdvancedScoringCriteria?.let {
            newAdvancedScoringCriteria.add(newAdvancedScoring)
            _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoringCriteria.isNullOrEmpty())
            _advancedScoringCriteria.onNext(newAdvancedScoringCriteria)
        }
    }

    fun editAdvancedScoringCriteria(newAdvancedScoring: String, index: Int) {
        val newAdvancedScoringCriteria = _advancedScoringCriteria.value?.toMutableList()
        newAdvancedScoringCriteria?.let {
            newAdvancedScoringCriteria[index] = newAdvancedScoring
            _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoringCriteria.isNullOrEmpty())
            _advancedScoringCriteria.onNext(newAdvancedScoringCriteria)
        }
    }

    fun deleteAdvancedScoringCriteria(index: Int) {
        val newAdvancedScoringCriteria = _advancedScoringCriteria.value?.toMutableList()
        newAdvancedScoringCriteria?.let {
            newAdvancedScoringCriteria.removeAt(index)
            _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoringCriteria.isNullOrEmpty())
            _advancedScoringCriteria.onNext(newAdvancedScoringCriteria)
        }
    }

    fun updateDefaultListOrder(newDefaultListOrder: ListOrder) {
        currentListsSettings?.rowOrder = newDefaultListOrder.value
        _defaultListOrder.onNext(newDefaultListOrder)
    }

    fun getScoreFormats() {
        _scoreFormats.onNext(ScoreFormat.values().toList().filterNot { it == ScoreFormat.UNKNOWN__ })
    }

    fun getListOrders() {
        _listOrders.onNext(ListOrder.values().toList())
    }

    private fun handleAdvancedScoringCriteriaVisibility() {
        _advancedScoringCriteriaVisibility.onNext(
            (_scoringSystem.value == ScoreFormat.POINT_100 || _scoringSystem.value == ScoreFormat.POINT_10_DECIMAL) &&
            _useAdvancedScoring.value == true
        )
    }
}