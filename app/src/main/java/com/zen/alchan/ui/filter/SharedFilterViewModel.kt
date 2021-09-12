package com.zen.alchan.ui.filter

import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class SharedFilterViewModel : BaseViewModel() {

    private val _newMediaFilter = PublishSubject.create<Pair<MediaFilter, FilteredList>>()
    val newMediaFilter: Observable<Pair<MediaFilter, FilteredList>>
        get() = _newMediaFilter

    private val _oldMediaFilter = BehaviorSubject.createDefault(MediaFilter())
    val oldMediaFilter: Observable<MediaFilter>
        get() = _oldMediaFilter

    private var currentFilteredList: FilteredList? = null

    override fun loadData() {
        // do nothing
    }

    fun updateMediaFilter(mediaFilter: MediaFilter, filteredList: FilteredList) {
        currentFilteredList = filteredList
        _oldMediaFilter.onNext(mediaFilter)
    }

    fun updateMediaFilterResult(newMediaFilter: MediaFilter) {
        currentFilteredList?.let {
            _newMediaFilter.onNext(newMediaFilter to it)
        }
    }

    enum class FilteredList {
        ANIME_MEDIA_LIST,
        MANGA_MEDIA_LIST,
        EXPLORE_LIST
    }
}