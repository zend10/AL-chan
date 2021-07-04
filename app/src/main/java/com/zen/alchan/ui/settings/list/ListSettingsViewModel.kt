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
import type.MediaType
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

    private val _splitCompletedAnimeSectionByFormat = BehaviorSubject.createDefault(false)
    val splitCompletedAnimeSectionByFormat: Observable<Boolean>
        get() = _splitCompletedAnimeSectionByFormat

    private val _splitCompletedMangaSectionByFormat = BehaviorSubject.createDefault(false)
    val splitCompletedMangaSectionByFormat: Observable<Boolean>
        get() = _splitCompletedMangaSectionByFormat

    private val _animeCustomLists = BehaviorSubject.createDefault<List<String>>(listOf())
    val animeCustomLists: Observable<List<String>>
        get() = _animeCustomLists

    private val _mangaCustomLists = BehaviorSubject.createDefault<List<String>>(listOf())
    val mangaCustomLists: Observable<List<String>>
        get() = _mangaCustomLists

    private val _animeSectionOrder = BehaviorSubject.createDefault<List<String>>(listOf())
    val animeSectionOrder: Observable<List<String>>
        get() = _animeSectionOrder

    private val _mangaSectionOrder = BehaviorSubject.createDefault<List<String>>(listOf())
    val mangaSectionOrder: Observable<List<String>>
        get() = _mangaSectionOrder

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

    private val _animeCustomListsNoItemTextVisibility = BehaviorSubject.createDefault(false)
    val animeCustomListsNoItemTextVisibility: Observable<Boolean>
        get() = _animeCustomListsNoItemTextVisibility

    private val _mangaCustomListsNoItemTextVisibility = BehaviorSubject.createDefault(false)
    val mangaCustomListsNoItemTextVisibility: Observable<Boolean>
        get() = _mangaCustomListsNoItemTextVisibility

    private val _animeSectionOrderItems = PublishSubject.create<List<String>>()
    val animeSectionOrderItems: Observable<List<String>>
        get() = _animeSectionOrderItems

    private val _mangaSectionOrderItems = PublishSubject.create<List<String>>()
    val mangaSectionOrderItems: Observable<List<String>>
        get() = _mangaSectionOrderItems

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

                    updateSplitCompletedAnimeSectionByFormat(mediaListOptions.animeList.splitCompletedSectionByFormat)
                    updateSplitCompletedMangaSectionByFormat(mediaListOptions.mangaList.splitCompletedSectionByFormat)

                    updateCustomLists(MediaType.ANIME, mediaListOptions.animeList.customLists)
                    updateCustomLists(MediaType.MANGA, mediaListOptions.mangaList.customLists)

                    updateSectionOrder(MediaType.ANIME, mediaListOptions.animeList.sectionOrder)
                    updateSectionOrder(MediaType.MANGA, mediaListOptions.mangaList.sectionOrder)

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

    fun updateSplitCompletedAnimeSectionByFormat(shouldSplitCompletedAnimeSectionByFormat: Boolean) {
        currentListsSettings?.animeList?.splitCompletedSectionByFormat = shouldSplitCompletedAnimeSectionByFormat
        _splitCompletedMangaSectionByFormat.onNext(shouldSplitCompletedAnimeSectionByFormat)
    }

    fun updateSplitCompletedMangaSectionByFormat(shouldSplitCompletedMangaSectionByFormat: Boolean) {
        currentListsSettings?.mangaList?.splitCompletedSectionByFormat = shouldSplitCompletedMangaSectionByFormat
        _splitCompletedMangaSectionByFormat.onNext(shouldSplitCompletedMangaSectionByFormat)
    }

    fun updateCustomLists(mediaType: MediaType, newCustomLists: List<String>) {
        when (mediaType) {
            MediaType.ANIME -> {
                currentListsSettings?.animeList?.customLists = newCustomLists
                _animeCustomListsNoItemTextVisibility.onNext(newCustomLists.isEmpty())
                _animeCustomLists.onNext(newCustomLists)
            }
            MediaType.MANGA -> {
                currentListsSettings?.mangaList?.customLists = newCustomLists
                _mangaCustomListsNoItemTextVisibility.onNext(newCustomLists.isEmpty())
                _mangaCustomLists.onNext(newCustomLists)
            }
        }

    }

    fun addCustomLists(mediaType: MediaType, newCustomListItem: String) {
        when (mediaType) {
            MediaType.ANIME -> {
                val newCustomLists = _animeCustomLists.value?.toMutableList()
                newCustomLists?.let {
                    newCustomLists.add(newCustomListItem)
                    currentListsSettings?.animeList?.customLists = newCustomLists
                    _animeCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _animeCustomLists.onNext(newCustomLists)
                }
            }
            MediaType.MANGA -> {
                val newCustomLists = _mangaCustomLists.value?.toMutableList()
                newCustomLists?.let {
                    newCustomLists.add(newCustomListItem)
                    currentListsSettings?.mangaList?.customLists = newCustomLists
                    _mangaCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _mangaCustomLists.onNext(newCustomLists)
                }
            }
        }
    }

    fun editCustomLists(mediaType: MediaType, newCustomListItem: String, index: Int) {
        when (mediaType) {
            MediaType.ANIME -> {
                val newCustomLists = _animeCustomLists.value?.toMutableList()
                newCustomLists?.let {
                    newCustomLists[index] = newCustomListItem
                    currentListsSettings?.animeList?.customLists= newCustomLists
                    _animeCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _animeCustomLists.onNext(newCustomLists)
                }
            }
            MediaType.MANGA -> {
                val newCustomLists = _mangaCustomLists.value?.toMutableList()
                newCustomLists?.let {
                    newCustomLists[index] = newCustomListItem
                    currentListsSettings?.mangaList?.customLists = newCustomLists
                    _mangaCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _mangaCustomLists.onNext(newCustomLists)
                }
            }
        }
    }

    fun deleteCustomLists(mediaType: MediaType, index: Int) {
        when (mediaType) {
            MediaType.ANIME -> {
                val newCustomLists = _animeCustomLists.value?.toMutableList()
                newCustomLists?.let {
                    newCustomLists.removeAt(index)
                    currentListsSettings?.animeList?.customLists = newCustomLists
                    _animeCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _animeCustomLists.onNext(newCustomLists)
                }
            }
            MediaType.MANGA -> {
                val newCustomLists = _mangaCustomLists.value?.toMutableList()
                newCustomLists?.let {
                    newCustomLists.removeAt(index)
                    currentListsSettings?.mangaList?.customLists = newCustomLists
                    _mangaCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _mangaCustomLists.onNext(newCustomLists)
                }
            }
        }
    }

    fun updateSectionOrder(mediaType: MediaType, newSectionOrder: List<String>) {

        // TODO: need to handle broken section order

        when (mediaType) {
            MediaType.ANIME -> {
                currentListsSettings?.animeList?.sectionOrder = newSectionOrder
                _animeSectionOrder.onNext(newSectionOrder)
            }
            MediaType.MANGA -> {
                currentListsSettings?.mangaList?.sectionOrder = newSectionOrder
                _mangaSectionOrder.onNext(newSectionOrder)
            }
        }
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

    fun loadListOrderItems() {
        val items = ArrayList<ListItem<ListOrder>>()
        items.add(ListItem(getListOrderStringResource(ListOrder.SCORE), ListOrder.SCORE))
        items.add(ListItem(getListOrderStringResource(ListOrder.TITLE), ListOrder.TITLE))
        items.add(ListItem(getListOrderStringResource(ListOrder.LAST_UPDATED), ListOrder.LAST_UPDATED))
        items.add(ListItem(getListOrderStringResource(ListOrder.LAST_ADDED), ListOrder.LAST_ADDED))
        _listOrderItems.onNext(items)
    }

    fun loadSectionOrderItems(mediaType: MediaType) {
        when (mediaType) {
            MediaType.ANIME -> {
                _animeSectionOrder.value?.let {
                    _animeSectionOrderItems.onNext(it)
                }
            }
            MediaType.MANGA -> {
                _mangaSectionOrder.value?.let {
                    _mangaSectionOrderItems.onNext(it)
                }
            }
        }
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