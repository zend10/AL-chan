package com.zen.alchan.ui.settings.list

import com.zen.R
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.ListActivityOption
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.ListOrder
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.getStringResource
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.zen.alchan.type.MediaListStatus
import com.zen.alchan.type.ScoreFormat

class ListSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel<Unit>() {

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

    private val _disableWatchingActivity = BehaviorSubject.createDefault(false)
    val disableWatchingActivity: Observable<Boolean>
        get() = _disableWatchingActivity

    private val _disablePlanningActivity = BehaviorSubject.createDefault(false)
    val disablePlanningActivity: Observable<Boolean>
        get() = _disablePlanningActivity

    private val _disableCompletedActivity = BehaviorSubject.createDefault(false)
    val disableCompletedActivity: Observable<Boolean>
        get() = _disableCompletedActivity

    private val _disableDroppedActivity = BehaviorSubject.createDefault(false)
    val disableDroppedActivity: Observable<Boolean>
        get() = _disableDroppedActivity

    private val _disablePausedActivity = BehaviorSubject.createDefault(false)
    val disablePausedActivity: Observable<Boolean>
        get() = _disablePausedActivity

    private val _disableRepeatingActivity = BehaviorSubject.createDefault(false)
    val disableRepeatingActivity: Observable<Boolean>
        get() = _disableRepeatingActivity

    private var viewer: User? = null
    private var currentListSettings: MediaListOptions? = null
    private var disabledListActivity: ArrayList<ListActivityOption> = arrayListOf(
        ListActivityOption(type = MediaListStatus.CURRENT),
        ListActivityOption(type = MediaListStatus.PLANNING),
        ListActivityOption(type = MediaListStatus.COMPLETED),
        ListActivityOption(type = MediaListStatus.DROPPED),
        ListActivityOption(type = MediaListStatus.PAUSED),
        ListActivityOption(type = MediaListStatus.REPEATING)
    )

    override fun loadData(param: Unit) {
        if (state == State.LOADED)
            return

        disposables.add(
            userRepository.getViewer(Source.CACHE)
                .applyScheduler()
                .subscribe {
                    viewer = it

                    val mediaListOptions = it.mediaListOptions
                    val options = it.options
                    currentListSettings = mediaListOptions


                    updateScoreFormat(mediaListOptions.scoreFormat ?: ScoreFormat.POINT_100)
                    updateAdvancedScoringEnabled(mediaListOptions.animeList.advancedScoringEnabled)
                    updateAdvancedScoring(mediaListOptions.animeList.advancedScoring)

                    val listOrder = ListOrder.values().find { listOrder -> listOrder.value == mediaListOptions.rowOrder } ?: ListOrder.TITLE
                    updateRowOrder(listOrder)

                    updateSplitCompletedAnimeSectionByFormat(mediaListOptions.animeList.splitCompletedSectionByFormat)
                    updateSplitCompletedMangaSectionByFormat(mediaListOptions.mangaList.splitCompletedSectionByFormat)

                    updateCustomLists(MediaType.ANIME, mediaListOptions.animeList.customLists)
                    updateCustomLists(MediaType.MANGA, mediaListOptions.mangaList.customLists)

                    updateSectionOrder(MediaType.ANIME, mediaListOptions.animeList.sectionOrder)
                    updateSectionOrder(MediaType.MANGA, mediaListOptions.mangaList.sectionOrder)

                    options.disabledListActivity.forEach { listActivityOption ->
                        listActivityOption.type?.let { mediaListStatus ->
                            updateDisableListActivity(mediaListStatus, listActivityOption.disabled)
                        }
                    }

                    state = State.LOADED
                }
        )
    }

    fun saveListSettings() {
        _loading.onNext(true)

        currentListSettings?.apply {
            disposables.add(
                userRepository.updateListSettings(
                    scoreFormat ?: ScoreFormat.POINT_100,
                    rowOrder,
                    animeList,
                    mangaList,
                    disabledListActivity
                )
                    .applyScheduler()
                    .doFinally { _loading.onNext(false) }
                    .subscribe(
                        {
                            viewer = it
                            _success.onNext(R.string.settings_saved)
                        },
                        {
                            _error.onNext(it.getStringResource())
                        }
                    )
            )
        }
    }

    fun updateScoreFormat(newScoreFormat: ScoreFormat) {
        currentListSettings?.scoreFormat = newScoreFormat
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
        currentListSettings?.animeList?.advancedScoringEnabled = shouldUseAdvancedScoring
        currentListSettings?.mangaList?.advancedScoringEnabled = shouldUseAdvancedScoring
        _advancedScoringEnabled.onNext(shouldUseAdvancedScoring)

        handleAdvancedScoringVisibility()
    }

    fun updateAdvancedScoring(newAdvancedScoring: List<String>) {
        currentListSettings?.animeList?.advancedScoring = newAdvancedScoring
        currentListSettings?.mangaList?.advancedScoring = newAdvancedScoring
        _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoring.isEmpty())
        _advancedScoring.onNext(newAdvancedScoring)
    }

    fun addAdvancedScoring(newAdvancedScoringItem: String) {
        val newAdvancedScoring = currentListSettings?.animeList?.advancedScoring?.toMutableList()
        newAdvancedScoring?.let {
            newAdvancedScoring.add(newAdvancedScoringItem)
            currentListSettings?.animeList?.advancedScoring = newAdvancedScoring
            currentListSettings?.mangaList?.advancedScoring = newAdvancedScoring
            _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoring.isNullOrEmpty())
            _advancedScoring.onNext(newAdvancedScoring)
        }
    }

    fun editAdvancedScoring(newAdvancedScoringItem: String, index: Int) {
        val newAdvancedScoring = currentListSettings?.animeList?.advancedScoring?.toMutableList()
        newAdvancedScoring?.let {
            newAdvancedScoring[index] = newAdvancedScoringItem
            currentListSettings?.animeList?.advancedScoring = newAdvancedScoring
            currentListSettings?.mangaList?.advancedScoring = newAdvancedScoring
            _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoring.isNullOrEmpty())
            _advancedScoring.onNext(newAdvancedScoring)
        }
    }

    fun deleteAdvancedScoringCriteria(index: Int) {
        val newAdvancedScoring = currentListSettings?.animeList?.advancedScoring?.toMutableList()
        newAdvancedScoring?.let {
            newAdvancedScoring.removeAt(index)
            currentListSettings?.animeList?.advancedScoring = newAdvancedScoring
            currentListSettings?.mangaList?.advancedScoring = newAdvancedScoring
            _advancedScoringNoItemTextVisibility.onNext(newAdvancedScoring.isNullOrEmpty())
            _advancedScoring.onNext(newAdvancedScoring)
        }
    }

    fun updateRowOrder(newRowOrder: ListOrder) {
        currentListSettings?.rowOrder = newRowOrder.value
        _rowOrder.onNext(newRowOrder)
    }

    fun updateSplitCompletedAnimeSectionByFormat(shouldSplitCompletedAnimeSectionByFormat: Boolean) {
        currentListSettings?.animeList?.splitCompletedSectionByFormat = shouldSplitCompletedAnimeSectionByFormat
        _splitCompletedAnimeSectionByFormat.onNext(shouldSplitCompletedAnimeSectionByFormat)
    }

    fun updateSplitCompletedMangaSectionByFormat(shouldSplitCompletedMangaSectionByFormat: Boolean) {
        currentListSettings?.mangaList?.splitCompletedSectionByFormat = shouldSplitCompletedMangaSectionByFormat
        _splitCompletedMangaSectionByFormat.onNext(shouldSplitCompletedMangaSectionByFormat)
    }

    fun updateCustomLists(mediaType: MediaType, newCustomLists: List<String>) {
        when (mediaType) {
            MediaType.ANIME -> {
                currentListSettings?.animeList?.customLists = newCustomLists
                _animeCustomListsNoItemTextVisibility.onNext(newCustomLists.isEmpty())
                _animeCustomLists.onNext(newCustomLists)
            }
            MediaType.MANGA -> {
                currentListSettings?.mangaList?.customLists = newCustomLists
                _mangaCustomListsNoItemTextVisibility.onNext(newCustomLists.isEmpty())
                _mangaCustomLists.onNext(newCustomLists)
            }
        }
    }

    fun addCustomLists(mediaType: MediaType, newCustomListItem: String) {
        when (mediaType) {
            MediaType.ANIME -> {
                val newCustomLists = currentListSettings?.animeList?.customLists?.toMutableList()
                newCustomLists?.let {
                    newCustomLists.add(newCustomListItem)
                    currentListSettings?.animeList?.customLists = newCustomLists
                    _animeCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _animeCustomLists.onNext(newCustomLists)
                }

                val newSectionOrder = currentListSettings?.animeList?.sectionOrder?.toMutableList()
                newSectionOrder?.let {
                    newSectionOrder.add(newCustomListItem)
                    updateSectionOrder(MediaType.ANIME, newSectionOrder)
                }
            }
            MediaType.MANGA -> {
                val newCustomLists = currentListSettings?.mangaList?.customLists?.toMutableList()
                newCustomLists?.let {
                    newCustomLists.add(newCustomListItem)
                    currentListSettings?.mangaList?.customLists = newCustomLists
                    _mangaCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _mangaCustomLists.onNext(newCustomLists)
                }

                val newSectionOrder = currentListSettings?.mangaList?.sectionOrder?.toMutableList()
                newSectionOrder?.let {
                    newSectionOrder.add(newCustomListItem)
                    updateSectionOrder(MediaType.MANGA, newSectionOrder)
                }
            }
        }
    }

    fun editCustomLists(mediaType: MediaType, newCustomListItem: String, index: Int) {
        when (mediaType) {
            MediaType.ANIME -> {
                val newCustomLists = currentListSettings?.animeList?.customLists?.toMutableList()
                val currentCustomListItem = currentListSettings?.animeList?.customLists?.get(index)
                newCustomLists?.let {
                    newCustomLists[index] = newCustomListItem
                    currentListSettings?.animeList?.customLists= newCustomLists
                    _animeCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _animeCustomLists.onNext(newCustomLists)
                }

                val newSectionOrder = currentListSettings?.animeList?.sectionOrder?.toMutableList()
                newSectionOrder?.let {
                    val indexAtSectionOrder = newSectionOrder.indexOf(currentCustomListItem)
                    if (indexAtSectionOrder != -1)
                        newSectionOrder[indexAtSectionOrder] = newCustomListItem
                    updateSectionOrder(MediaType.ANIME, newSectionOrder)
                }
            }
            MediaType.MANGA -> {
                val newCustomLists = currentListSettings?.mangaList?.customLists?.toMutableList()
                val currentCustomListItem = currentListSettings?.mangaList?.customLists?.get(index)
                newCustomLists?.let {
                    newCustomLists[index] = newCustomListItem
                    currentListSettings?.mangaList?.customLists = newCustomLists
                    _mangaCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _mangaCustomLists.onNext(newCustomLists)
                }

                val newSectionOrder = currentListSettings?.mangaList?.sectionOrder?.toMutableList()
                newSectionOrder?.let {
                    val indexAtSectionOrder = newSectionOrder.indexOf(currentCustomListItem)
                    if (indexAtSectionOrder != -1)
                        newSectionOrder[indexAtSectionOrder] = newCustomListItem
                    updateSectionOrder(MediaType.MANGA, newSectionOrder)
                }
            }
        }
    }

    fun deleteCustomLists(mediaType: MediaType, index: Int) {
        when (mediaType) {
            MediaType.ANIME -> {
                val newCustomLists = currentListSettings?.animeList?.customLists?.toMutableList()
                val currentCustomListItem = currentListSettings?.animeList?.customLists?.get(index)
                newCustomLists?.let {
                    newCustomLists.removeAt(index)
                    currentListSettings?.animeList?.customLists = newCustomLists
                    _animeCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _animeCustomLists.onNext(newCustomLists)
                }

                val newSectionOrder = currentListSettings?.animeList?.sectionOrder?.toMutableList()
                newSectionOrder?.let {
                    val indexAtSectionOrder = newSectionOrder.indexOf(currentCustomListItem)
                    if (indexAtSectionOrder != -1)
                        newSectionOrder.removeAt(indexAtSectionOrder)
                    updateSectionOrder(MediaType.ANIME, newSectionOrder)
                }
            }
            MediaType.MANGA -> {
                val newCustomLists = currentListSettings?.mangaList?.customLists?.toMutableList()
                val currentCustomListItem = currentListSettings?.mangaList?.customLists?.get(index)
                newCustomLists?.let {
                    newCustomLists.removeAt(index)
                    currentListSettings?.mangaList?.customLists = newCustomLists
                    _mangaCustomListsNoItemTextVisibility.onNext(newCustomLists.isNullOrEmpty())
                    _mangaCustomLists.onNext(newCustomLists)
                }

                val newSectionOrder = currentListSettings?.mangaList?.sectionOrder?.toMutableList()
                newSectionOrder?.let {
                    val indexAtSectionOrder = newSectionOrder.indexOf(currentCustomListItem)
                    if (indexAtSectionOrder != -1)
                        newSectionOrder.removeAt(indexAtSectionOrder)
                    updateSectionOrder(MediaType.MANGA, newSectionOrder)
                }
            }
        }
    }

    fun updateSectionOrder(mediaType: MediaType, newSectionOrder: List<String>) {
        when (mediaType) {
            MediaType.ANIME -> {
                currentListSettings?.animeList?.sectionOrder = newSectionOrder
                _animeSectionOrder.onNext(newSectionOrder)
            }
            MediaType.MANGA -> {
                currentListSettings?.mangaList?.sectionOrder = newSectionOrder
                _mangaSectionOrder.onNext(newSectionOrder)
            }
        }
    }

    fun updateDisableListActivity(mediaListStatus: MediaListStatus, shouldDisable: Boolean) {
        val index = disabledListActivity.indexOfFirst { it.type == mediaListStatus }
        if (index == -1) return
        disabledListActivity[index] = ListActivityOption(shouldDisable, mediaListStatus)
        val disableListActivitySubject = when (mediaListStatus) {
            MediaListStatus.CURRENT -> _disableWatchingActivity
            MediaListStatus.PLANNING -> _disablePlanningActivity
            MediaListStatus.COMPLETED -> _disableCompletedActivity
            MediaListStatus.DROPPED -> _disableDroppedActivity
            MediaListStatus.PAUSED -> _disablePausedActivity
            MediaListStatus.REPEATING -> _disableRepeatingActivity
            else -> return
        }
        disableListActivitySubject.onNext(shouldDisable)
    }

    fun loadScoreFormatItems() {
        val items = ArrayList<ListItem<ScoreFormat>>()
        items.add(ListItem(ScoreFormat.POINT_100.getStringResource(), ScoreFormat.POINT_100))
        items.add(ListItem(ScoreFormat.POINT_10_DECIMAL.getStringResource(), ScoreFormat.POINT_10_DECIMAL))
        items.add(ListItem(ScoreFormat.POINT_10.getStringResource(), ScoreFormat.POINT_10))
        items.add(ListItem(ScoreFormat.POINT_5.getStringResource(), ScoreFormat.POINT_5))
        items.add(ListItem(ScoreFormat.POINT_3.getStringResource(), ScoreFormat.POINT_3))
        _scoreFormatItems.onNext(items)
    }

    fun loadListOrderItems() {
        val items = ArrayList<ListItem<ListOrder>>()
        items.add(ListItem(ListOrder.SCORE.getStringResource(), ListOrder.SCORE))
        items.add(ListItem(ListOrder.TITLE.getStringResource(), ListOrder.TITLE))
        items.add(ListItem(ListOrder.LAST_UPDATED.getStringResource(), ListOrder.LAST_UPDATED))
        items.add(ListItem(ListOrder.LAST_ADDED.getStringResource(), ListOrder.LAST_ADDED))
        _listOrderItems.onNext(items)
    }

    fun loadSectionOrderItems(mediaType: MediaType) {
        when (mediaType) {
            MediaType.ANIME -> {
                currentListSettings?.animeList?.sectionOrder?.let {
                    _animeSectionOrderItems.onNext(it)
                }
            }
            MediaType.MANGA -> {
                currentListSettings?.mangaList?.sectionOrder?.let {
                    _mangaSectionOrderItems.onNext(it)
                }
            }
        }
    }

    fun resetSectionOrder(mediaType: MediaType) {
        val defaultSectionOrder = ArrayList<String>()

        when (mediaType) {
            MediaType.ANIME -> {
                defaultSectionOrder.addAll(
                    if (currentListSettings?.animeList?.splitCompletedSectionByFormat == true) {
                        listOf(
                            "Watching",
                            "Rewatching",
                            "Completed TV",
                            "Completed Movie",
                            "Completed OVA",
                            "Completed ONA",
                            "Completed TV Short",
                            "Completed Special",
                            "Completed Music",
                            "Paused",
                            "Dropped",
                            "Planning"
                        )
                    } else {
                        listOf(
                            "Watching",
                            "Rewatching",
                            "Completed",
                            "Paused",
                            "Dropped",
                            "Planning"
                        )
                    }
                )

                defaultSectionOrder.addAll(currentListSettings?.animeList?.customLists ?: listOf())
            }
            MediaType.MANGA -> {
                defaultSectionOrder.addAll(
                    if (currentListSettings?.mangaList?.splitCompletedSectionByFormat == true) {
                        listOf(
                            "Reading",
                            "Rereading",
                            "Completed Manga",
                            "Completed Novel",
                            "Completed One Shot",
                            "Paused",
                            "Dropped",
                            "Planning"
                        )
                    } else {
                        listOf(
                            "Reading",
                            "Rereading",
                            "Completed",
                            "Paused",
                            "Dropped",
                            "Planning"
                        )
                    }
                )

                defaultSectionOrder.addAll(currentListSettings?.mangaList?.customLists ?: listOf())
            }
        }

        updateSectionOrder(mediaType, defaultSectionOrder)
    }

    private fun handleAdvancedScoringVisibility() {
        _advancedScoringVisibility.onNext(
            (currentListSettings?.scoreFormat == ScoreFormat.POINT_100 || currentListSettings?.scoreFormat == ScoreFormat.POINT_10_DECIMAL) &&
                    currentListSettings?.animeList?.advancedScoringEnabled == true
        )
    }
}