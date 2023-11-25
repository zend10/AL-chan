package com.zen.alchan.ui.seasonal

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getNonUnknownValues
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.SeasonalAdapterComponent
import com.zen.alchan.helper.pojo.SeasonalItem
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.zen.alchan.type.MediaFormat
import com.zen.alchan.type.MediaListStatus
import com.zen.alchan.type.MediaSeason
import com.zen.alchan.type.UserTitleLanguage

class SeasonalViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository,
    private val mediaListRepository: MediaListRepository
) : BaseViewModel<Unit>() {

    private val _seasonalAdapterComponent = PublishSubject.create<SeasonalAdapterComponent>()
    val seasonalAdapterComponent: Observable<SeasonalAdapterComponent>
        get() = _seasonalAdapterComponent

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private val _seasonalItems = BehaviorSubject.createDefault<List<SeasonalItem>>(listOf())
    val seasonalItems: Observable<List<SeasonalItem>>
        get() = _seasonalItems

    private val _year = BehaviorSubject.createDefault(TimeUtil.getCurrentYear())
    val year: Observable<Int>
        get() = _year

    private val _season = BehaviorSubject.createDefault(TimeUtil.getCurrentSeason())
    val season: Observable<MediaSeason>
        get() = _season

    private val _sort = BehaviorSubject.createDefault(Sort.POPULARITY)
    val sort: Observable<Sort>
        get() = _sort

    private val _orderByDescending = BehaviorSubject.createDefault(true)
    val orderByDescending: Observable<Boolean>
        get() = _orderByDescending

    private val _hideSeriesOnList = BehaviorSubject.createDefault(false)
    val hideSeriesOnList: Observable<Boolean>
        get() = _hideSeriesOnList

    private val _onlyShowSeriesOnList = BehaviorSubject.createDefault(false)
    val onlyShowSeriesOnList: Observable<Boolean>
        get() = _onlyShowSeriesOnList

    private val _showAdult = BehaviorSubject.createDefault(false)
    val showAdult: Observable<Boolean>
        get() = _showAdult

    private val _yearList = PublishSubject.create<List<ListItem<Int>>>()
    val yearList: Observable<List<ListItem<Int>>>
        get() = _yearList

    private val _seasonList = PublishSubject.create<List<ListItem<MediaSeason>>>()
    val seasonList: Observable<List<ListItem<MediaSeason>>>
        get() = _seasonList

    private val _sortList = PublishSubject.create<List<ListItem<Sort>>>()
    val sortList: Observable<List<ListItem<Sort>>>
        get() = _sortList

    private val _orderByList = PublishSubject.create<List<ListItem<Boolean>>>()
    val orderByList: Observable<List<ListItem<Boolean>>>
        get() = _orderByList

    private val _listTypeList = PublishSubject.create<List<ListItem<ListType>>>()
    val listTypeList: Observable<List<ListItem<ListType>>>
        get() = _listTypeList

    private var previousPagesSeasonals = ArrayList<Media>()
    private var appSetting = AppSetting()
    private var mediaTitleLanguage = UserTitleLanguage.ROMAJI

    private val formatOrder = listOf(
        MediaFormat.TV,
        MediaFormat.TV_SHORT,
        MediaFormat.MOVIE,
        MediaFormat.OVA,
        MediaFormat.ONA,
        MediaFormat.SPECIAL,
        MediaFormat.MUSIC
    )

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                Observable.zip(
                    userRepository.getAppSetting(),
                    userRepository.getViewer(Source.CACHE)
                ) { appSetting, user ->
                    appSetting to user
                }
                    .applyScheduler()
                    .subscribe { (appSetting, user) ->
                        this.appSetting = appSetting
                        this.mediaTitleLanguage = user.options.titleLanguage ?: UserTitleLanguage.ROMAJI
                        _seasonalAdapterComponent.onNext(SeasonalAdapterComponent(ListType.LINEAR, appSetting))
                        loadSeasonal()
                    }
            )
        }
    }

    fun reloadData() {
        loadSeasonal()
    }

    private fun loadSeasonal(page: Int = 1) {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            contentRepository.getSeasonal(page, _year.value ?: TimeUtil.getCurrentYear(), _season.value ?: TimeUtil.getCurrentSeason(), _sort.value ?: Sort.POPULARITY, mediaTitleLanguage, _orderByDescending.value ?: true, getOnlyShowOnList(), _showAdult.value ?: false)
                .applyScheduler()
                .doFinally {
                    if (state != State.LOADING)
                        _emptyLayoutVisibility.onNext(_seasonalItems.value.isNullOrEmpty())
                }
                .subscribe(
                    {
                        if (it.pageInfo.hasNextPage) {
                            previousPagesSeasonals.addAll(it.data)
                            loadSeasonal(page + 1)
                        } else {
                            previousPagesSeasonals.addAll(it.data)

                            val items = ArrayList<SeasonalItem>()
                            val previousPagesSeasonalsGroup = previousPagesSeasonals.groupBy { it.format }
                            formatOrder.forEach { format ->
                                previousPagesSeasonalsGroup[format]?.let { media ->
                                    items.add(SeasonalItem(title = format.getString(), viewType = SeasonalItem.VIEW_TYPE_TITLE))
                                    items.addAll(media.map { SeasonalItem(media = it, viewType = SeasonalItem.VIEW_TYPE_MEDIA) })
                                }
                            }

                            _seasonalItems.onNext(items)
                            state = State.LOADED
                            previousPagesSeasonals.clear()
                            _loading.onNext(false)
                        }
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                        previousPagesSeasonals.clear()
                        _loading.onNext(false)
                    }
                )
        )
    }

    private fun getOnlyShowOnList(): Boolean? {
        return if (_hideSeriesOnList.value == true)
            false
        else if (_onlyShowSeriesOnList.value == true)
            true
        else
            null
    }

    fun loadYearList() {
        _yearList.onNext(
            (TimeUtil.getCurrentYear() + 1 downTo 1950).map {
                ListItem(it.toString(), it)
            }
        )
    }

    fun updateYear(newYear: Int) {
        _year.onNext(newYear)
        reloadData()
    }

    fun loadSeasonList() {
        _seasonList.onNext(
            getNonUnknownValues<MediaSeason>().map {
                ListItem(it.getString(), it)
            }
        )
    }

    fun updateSeason(newSeason: MediaSeason) {
        _season.onNext(newSeason)
        reloadData()
    }

    fun loadSortList() {
        val sortList = ArrayList<Sort>()
        sortList.add(Sort.TITLE)
        sortList.add(Sort.SCORE)
        sortList.add(Sort.RELEASE_DATE)
        sortList.add(Sort.AVERAGE_SCORE)
        sortList.add(Sort.POPULARITY)
        sortList.add(Sort.FAVORITES)
        sortList.add(Sort.TRENDING)
        _sortList.onNext(
            sortList.map {
                ListItem(it.getStringResource(), it)
            }
        )
    }

    fun updateSort(newSort: Sort) {
        _sort.onNext(newSort)
        reloadData()
    }

    fun loadOrderByList() {
        val orderByList = ArrayList<ListItem<Boolean>>()
        orderByList.add(ListItem(R.string.descending, true))
        orderByList.add(ListItem(R.string.ascending, false))
        _orderByList.onNext(orderByList)
    }

    fun updateOrderBy(newOrderBy: Boolean) {
        _orderByDescending.onNext(newOrderBy)
        reloadData()
    }

    fun updateHideSeriesOnList(shouldHideSeriesOnList: Boolean) {
        if (shouldHideSeriesOnList)
            _onlyShowSeriesOnList.onNext(false)

        _hideSeriesOnList.onNext(shouldHideSeriesOnList)
        reloadData()
    }

    fun updateOnlyShowSeriesOnList(shouldOnlyShowSeriesOnList: Boolean) {
        if (shouldOnlyShowSeriesOnList)
            _hideSeriesOnList.onNext(false)

        _onlyShowSeriesOnList.onNext(shouldOnlyShowSeriesOnList)
        reloadData()
    }

    fun updateShowAdultContent(shouldShowAdultContent: Boolean) {
        _showAdult.onNext(shouldShowAdultContent)
        reloadData()
    }

    fun loadListTypeList() {
        _listTypeList.onNext(
            listOf(
                ListItem(ListType.LINEAR.getString(), ListType.LINEAR),
                ListItem(ListType.GRID.getString(), ListType.GRID)
            )
        )
    }

    fun updateListType(newListType: ListType) {
        _seasonalAdapterComponent.onNext(
            SeasonalAdapterComponent(newListType, appSetting)
        )
        _seasonalItems.value?.let {
            _seasonalItems.onNext(it)
        }
    }

    fun addToPlanning(media: Media) {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            mediaListRepository.updateMediaListStatus(MediaType.ANIME, media.getId(), MediaListStatus.PLANNING)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        val currentSeasonalItems = ArrayList(_seasonalItems.value ?: listOf())
                        val index = currentSeasonalItems.indexOfFirst { it.media.getId() == media.getId() }
                        val editedItem = currentSeasonalItems[index]
                        currentSeasonalItems[index] = editedItem.copy(media = editedItem.media.copy(mediaListEntry = it))
                        _seasonalItems.onNext(currentSeasonalItems)
                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }
}