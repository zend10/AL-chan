package com.zen.alchan.ui.settings.list

import com.zen.alchan.R
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.ListOrder
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.ScoreFormat

class ListSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _scoreFormat = BehaviorSubject.createDefault(ScoreFormat.POINT_100)
    val scoreFormat: Observable<ScoreFormat>
        get() = _scoreFormat

    private val _advancedScoringEnabled = BehaviorSubject.createDefault(false)
    val advancedScoringEnabled: Observable<Boolean>
        get() = _advancedScoringEnabled

    private val _advancedScoring = BehaviorSubject.createDefault<List<String>>(listOf())
    val advancedScoring: Observable<List<String>>
        get() = _advancedScoring

    private val _rowOrder = BehaviorSubject.createDefault(ListOrder.TITLE)
    val rowOrder: Observable<ListOrder>
        get() = _rowOrder

    private val _animeCustomLists = BehaviorSubject.createDefault<List<String>>(listOf())
    val animeCustomLists: Observable<List<String>>
        get() = _animeCustomLists

    private val _mangaCustomLists = BehaviorSubject.createDefault<List<String>>(listOf())
    val mangaCustomLists: Observable<List<String>>
        get() = _mangaCustomLists





    private val _scoreFormatItems = PublishSubject.create<List<ListItem<ScoreFormat>>>()
    val scoreFormatItems: Observable<List<ListItem<ScoreFormat>>>
        get() = _scoreFormatItems

    private val _useAdvancedScoringVisibility = BehaviorSubject.createDefault(false)
    val useAdvancedScoringVisibility: Observable<Boolean>
        get() = _useAdvancedScoringVisibility

    private val _advancedScoringVisibility = BehaviorSubject.createDefault(false)
    val advancedScoringVisibility: Observable<Boolean>
        get() = _advancedScoringVisibility

    private val _advancedScoringNoItemTextVisibility = BehaviorSubject.createDefault(false)
    val advancedScoringNoItemTextVisibility: Observable<Boolean>
        get() = _advancedScoringNoItemTextVisibility

    private val _listOrderItems = PublishSubject.create<List<ListItem<ListOrder>>>()
    val listOrderItems: Observable<List<ListItem<ListOrder>>>
        get() = _listOrderItems

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

                    updateScoreFormat(mediaListOptions.scoreFormat ?: ScoreFormat.POINT_100)
                    updateAdvancedScoringEnabled(mediaListOptions.animeList.advancedScoringEnabled)
                    updateAdvancedScoring(mediaListOptions.animeList.advancedScoring)

                    try {
                        val listOrder = ListOrder.valueOf(mediaListOptions.rowOrder)
                        updateRowOrder(listOrder)
                    } catch (e: IllegalArgumentException) {
                        updateRowOrder(ListOrder.TITLE)
                    }

                    updateAnimeCustomLists(mediaListOptions.animeList.customLists)
                    updateMangaCustomLists(mediaListOptions.mangaList.customLists)

                    state = State.LOADED
                }
        )
    }

    fun updateScoreFormat(newScoreFormat: ScoreFormat) {
        currentListsSettings?.scoreFormat = newScoreFormat
        _scoreFormat.onNext(newScoreFormat)

        when (newScoreFormat) {
            ScoreFormat.POINT_100, ScoreFormat.POINT_10_DECIMAL -> {
                _useAdvancedScoringVisibility.onNext(true)
            }
            else -> {
                _useAdvancedScoringVisibility.onNext(false)
                _advancedScoringEnabled.onNext(false)
            }
        }

        handleAdvancedScoringVisibility()
    }

    fun updateAdvancedScoringEnabled(shouldUseAdvancedScoring: Boolean) {
        currentListsSettings?.animeList?.advancedScoringEnabled = shouldUseAdvancedScoring
        currentListsSettings?.mangaList?.advancedScoringEnabled = shouldUseAdvancedScoring
        _advancedScoringEnabled.onNext(shouldUseAdvancedScoring)

        handleAdvancedScoringVisibility()
    }

    fun updateAdvancedScoring(newAdvancedScoring: List<String>) {
        currentListsSettings?.animeList?.advancedScoring = newAdvancedScoring
        currentListsSettings?.mangaList?.advancedScoring = newAdvancedScoring
        _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoring.isEmpty())
        _advancedScoring.onNext(newAdvancedScoring)
    }

    fun addAdvancedScoring(newAdvancedScoringItem: String) {
        val newAdvancedScoring = _advancedScoring.value?.toMutableList()
        newAdvancedScoring?.let {
            newAdvancedScoring.add(newAdvancedScoringItem)
            currentListsSettings?.animeList?.advancedScoring = newAdvancedScoring
            currentListsSettings?.mangaList?.advancedScoring = newAdvancedScoring
            _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoring.isNullOrEmpty())
            _advancedScoring.onNext(newAdvancedScoring)
        }
    }

    fun editAdvancedScoring(newAdvancedScoringItem: String, index: Int) {
        val newAdvancedScoring = _advancedScoring.value?.toMutableList()
        newAdvancedScoring?.let {
            newAdvancedScoring[index] = newAdvancedScoringItem
            currentListsSettings?.animeList?.advancedScoring = newAdvancedScoring
            currentListsSettings?.mangaList?.advancedScoring = newAdvancedScoring
            _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoring.isNullOrEmpty())
            _advancedScoring.onNext(newAdvancedScoring)
        }
    }

    fun deleteAdvancedScoringCriteria(index: Int) {
        val newAdvancedScoring = _advancedScoring.value?.toMutableList()
        newAdvancedScoring?.let {
            newAdvancedScoring.removeAt(index)
            currentListsSettings?.animeList?.advancedScoring = newAdvancedScoring
            currentListsSettings?.mangaList?.advancedScoring = newAdvancedScoring
            _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoring.isNullOrEmpty())
            _advancedScoring.onNext(newAdvancedScoring)
        }
    }

    fun updateRowOrder(newRowOrder: ListOrder) {
        currentListsSettings?.rowOrder = newRowOrder.value
        _rowOrder.onNext(newRowOrder)
    }

    fun updateAnimeCustomLists(newCustomLists: List<String>) {
        currentListsSettings?.animeList?.customLists = newCustomLists
    }

    fun updateMangaCustomLists(newCustomLists: List<String>) {
        currentListsSettings?.mangaList?.customLists = newCustomLists
    }

    fun loadScoreFormatItems() {
        val items = ArrayList<ListItem<ScoreFormat>>()
        items.add(ListItem(getScoreFormatStringResource(ScoreFormat.POINT_100), ScoreFormat.POINT_100))
        items.add(ListItem(getScoreFormatStringResource(ScoreFormat.POINT_10_DECIMAL), ScoreFormat.POINT_10_DECIMAL))
        items.add(ListItem(getScoreFormatStringResource(ScoreFormat.POINT_10), ScoreFormat.POINT_10))
        items.add(ListItem(getScoreFormatStringResource(ScoreFormat.POINT_5), ScoreFormat.POINT_5))
        items.add(ListItem(getScoreFormatStringResource(ScoreFormat.POINT_3), ScoreFormat.POINT_3))
        _scoreFormatItems.onNext(items)
    }

    fun getListOrders() {
        val items = ArrayList<ListItem<ListOrder>>()
        items.add(ListItem(getListOrderStringResource(ListOrder.SCORE), ListOrder.SCORE))
        items.add(ListItem(getListOrderStringResource(ListOrder.TITLE), ListOrder.TITLE))
        items.add(ListItem(getListOrderStringResource(ListOrder.LAST_UPDATED), ListOrder.LAST_UPDATED))
        items.add(ListItem(getListOrderStringResource(ListOrder.LAST_ADDED), ListOrder.LAST_ADDED))
        _listOrderItems.onNext(items)
    }

    private fun handleAdvancedScoringVisibility() {
        _advancedScoringVisibility.onNext(
            (_scoreFormat.value == ScoreFormat.POINT_100 || _scoreFormat.value == ScoreFormat.POINT_10_DECIMAL) &&
            _advancedScoringEnabled.value == true
        )
    }

    fun getScoreFormatStringResource(scoreFormat: ScoreFormat): Int {
        return when (scoreFormat) {
            ScoreFormat.POINT_100 -> R.string.hundred_point
            ScoreFormat.POINT_10_DECIMAL -> R.string.ten_point_decimal
            ScoreFormat.POINT_10 -> R.string.ten_point
            ScoreFormat.POINT_5 -> R.string.five_star
            ScoreFormat.POINT_3 -> R.string.three_point_smiley
            else -> R.string.hundred_point
        }
    }

    fun getListOrderStringResource(listOrder: ListOrder): Int {
        return when (listOrder) {
            ListOrder.SCORE -> R.string.score
            ListOrder.TITLE -> R.string.title
            ListOrder.LAST_UPDATED -> R.string.last_updated
            ListOrder.LAST_ADDED -> R.string.last_added
        }
    }
}