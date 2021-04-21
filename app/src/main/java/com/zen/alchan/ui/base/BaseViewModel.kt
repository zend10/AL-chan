package com.zen.alchan.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class BaseViewModel : ViewModel() {

    protected val disposables = CompositeDisposable()

    protected val navigationSubject =
        PublishSubject.create<Pair<NavigationManager.Page, List<String>>>()

    val navigation: Observable<Pair<NavigationManager.Page, List<String>>>
        get() = navigationSubject

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}