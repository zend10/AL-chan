package com.zen.alchan.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class BaseViewModel<T> : ViewModel(), ViewModelContract<T> {

    protected val disposables = CompositeDisposable()

    protected val _loading = BehaviorSubject.createDefault(false)
    val loading: Observable<Boolean>
        get() = _loading

    protected val _success = PublishSubject.create<Int>()
    val success: Observable<Int>
        get() = _success

    protected val _error = PublishSubject.create<Int>()
    val error: Observable<Int>
        get() = _error

    protected val _isAuthenticated = BehaviorSubject.createDefault(false)
    val isAuthenticated: Observable<Boolean>
        get() = _isAuthenticated

    protected val _closePage = PublishSubject.create<Unit>()
    val closePage: Observable<Unit>
        get() = _closePage

    protected var state = State.INIT

    protected fun loadOnce(action: () -> Unit) {
        if (state != State.INIT)
            return

        state = State.LOADING
        action()
    }

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