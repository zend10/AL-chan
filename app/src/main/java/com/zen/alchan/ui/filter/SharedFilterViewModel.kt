package com.zen.alchan.ui.filter

import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class SharedFilterViewModel : BaseViewModel() {

    private val _mediaFilterResult = PublishSubject.create<Pair<MediaFilter, FilterList>>()
    val mediaFilterResult: Observable<Pair<MediaFilter, FilterList>>
        get() = _mediaFilterResult

    private val _mediaFilter = BehaviorSubject.createDefault(MediaFilter.EMPTY_MEDIA_FILTER)
    val mediaFilter: Observable<MediaFilter>
        get() = _mediaFilter

    private var currentFilterList: FilterList? = null

    override fun loadData() {
        // do nothing
    }

    fun updateMediaFilter(newMediaFilter: MediaFilter, filterList: FilterList) {
        currentFilterList = filterList
        _mediaFilter.onNext(newMediaFilter)
    }

    fun updateMediaFilterResult() {
        val savedFilter = _mediaFilter.value ?: MediaFilter.EMPTY_MEDIA_FILTER
        currentFilterList?.let {
            _mediaFilterResult.onNext(savedFilter to it)
        }
    }

    enum class FilterList {
        ANIME_MEDIA_LIST,
        MANGA_MEDIA_LIST,
        EXPLORE_LIST
    }
}