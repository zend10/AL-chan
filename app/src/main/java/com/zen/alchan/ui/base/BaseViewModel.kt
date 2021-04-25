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

    protected val isAuthenticatedSubject = PublishSubject.create<Boolean>()

    val loading: Observable<Boolean>
        get() = loadingSubject

    val error: Observable<Int>
        get() = errorSubject

    val isAuthenticated: Observable<Boolean>
        get() = isAuthenticatedSubject

    protected var state = State.INIT

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected enum class State {
        INIT,
        LOADING,
        LOADED,
        ERROR
    }
}