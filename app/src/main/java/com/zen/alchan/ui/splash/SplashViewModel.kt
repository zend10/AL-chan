package com.zen.alchan.ui.splash

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.NavigationManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class SplashViewModel(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    private val isLoggedInSubject = PublishSubject.create<Boolean>()

    val isLoggedIn: Observable<Boolean>
        get() = isLoggedInSubject

    fun checkIsLoggedIn() {
        disposables.add(
            authenticationRepository.getIsAuthenticated()
                .zipWith(authenticationRepository.getIsLoggedIn()) { isAuthenticated, isLoggedIn ->
                    isAuthenticated to isLoggedIn
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.first)
                        loadViewerData()
                    else
                        isLoggedInSubject.onNext(it.second)
                }
        )
    }

    private fun loadViewerData() {
        disposables.add(
            authenticationRepository.viewer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isLoggedInSubject.onNext(it != User.EMPTY_USER)
                }
        )

        authenticationRepository.getViewerData()
    }
}