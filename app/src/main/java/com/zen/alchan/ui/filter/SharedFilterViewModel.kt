package com.zen.alchan.ui.filter

import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class SharedFilterViewModel : BaseViewModel() {

    private val _newMediaFilter = PublishSubject.create<Pair<MediaFilter, FilterList>>()
    val newMediaFilter: Observable<Pair<MediaFilter, FilterList>>
        get() = _newMediaFilter

    private val _oldMediaFilter = BehaviorSubject.createDefault(MediaFilter())
    val oldMediaFilter: Observable<MediaFilter>
        get() = _oldMediaFilter

    private var currentFilterList: FilterList? = null

    override fun loadData() {
        // do nothing
    }

    fun updateMediaFilter(mediaFilter: MediaFilter, filterList: FilterList) {
        currentFilterList = filterList
        _oldMediaFilter.onNext(mediaFilter)
    }

    fun updateMediaFilterResult(newMediaFilter: MediaFilter) {
        currentFilterList?.let {
            _newMediaFilter.onNext(newMediaFilter to it)
        }
    }

    enum class FilterList {
        ANIME_MEDIA_LIST,
        MANGA_MEDIA_LIST,
        EXPLORE_LIST
    }
}