package com.zen.alchan.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class BaseViewModel : ViewModel() {

    protected val disposables = CompositeDisposable()

    protected val loadingSubject = BehaviorSubject.createDefault(false)

    protected val errorSubject = PublishSubject.create<Int>()

    val loading: Observable<Boolean>
        get() = loadingSubject

    val error: Observable<Int>
        get() = errorSubject

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}