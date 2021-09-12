package com.zen.alchan.ui.reorder

import androidx.lifecycle.ViewModel
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class SharedReorderViewModel : BaseViewModel() {

    private val _orderedList = PublishSubject.create<Pair<List<String>, ReorderList>>()
    val orderedList: Observable<Pair<List<String>, ReorderList>>
        get() = _orderedList

    private val _unorderedList = BehaviorSubject.createDefault<List<String>>(listOf())
    val unorderedList: Observable<List<String>>
        get() = _unorderedList

    private var currentReorderList: ReorderList? = null

    override fun loadData() {
        // do nothing
    }

    fun updateUnorderedList(newList: List<String>, reorderList: ReorderList) {
        currentReorderList = reorderList
        _unorderedList.onNext(newList)
    }

    // TODO: might need to refactor this to follow SharedFilterViewModel's implementation
    fun updateOrderedList() {
        val savedList = _unorderedList.value ?: listOf()
        currentReorderList?.let {
            _orderedList.onNext(savedList to it)
        }
    }

    enum class ReorderList {
        ANIME_SECTION_ORDER,
        MANGA_SECTION_ORDER
    }
}