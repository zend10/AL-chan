package com.zen.alchan.ui.reorder

import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ReorderViewModel : BaseViewModel<ReorderParam>() {

    private val _itemList = BehaviorSubject.createDefault(listOf<String>())
    val itemList: Observable<List<String>>
        get() = _itemList

    private val _reorderResult = PublishSubject.create<List<String>>()
    val reorderResult: Observable<List<String>>
        get() = _reorderResult

    override fun loadData(param: ReorderParam) {
        loadOnce {
            _itemList.onNext(param.itemList)
        }
    }

    fun saveOrder() {
        _reorderResult.onNext(_itemList.value ?: listOf())
    }
}