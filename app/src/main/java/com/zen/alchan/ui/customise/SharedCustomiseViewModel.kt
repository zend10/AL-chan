package com.zen.alchan.ui.customise

import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SharedCustomiseViewModel : BaseViewModel() {

    private val _newListStyle = PublishSubject.create<Pair<ListStyle, CustomisedList>>()
    val newListStyle: Observable<Pair<ListStyle, CustomisedList>>
        get() = _newListStyle

    private var currentCustomisedList: CustomisedList? = null

    override fun loadData() {
        // do nothing
    }

    fun updateListStyle(listStyle: ListStyle, customisedList: CustomisedList) {
        currentCustomisedList = customisedList
        // not doing anything with the listStyle for not because we're getting current style from local storage
        // still doing it this way for consistency sake
    }

    fun updateListStyleResult(newListStyle: ListStyle) {
        currentCustomisedList?.let {
            _newListStyle.onNext(newListStyle to it)
        }
    }

    enum class CustomisedList {
        ANIME_LIST,
        MANGA_LIST
    }
}