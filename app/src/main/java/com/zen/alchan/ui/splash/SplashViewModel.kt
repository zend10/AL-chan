package com.zen.alchan.ui.splash

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class SplashViewModel(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    private val isLoggedInSubject = PublishSubject.create<Boolean>()

    val isLoggedIn: Observable<Boolean>
        get() = isLoggedInSubject

    override fun loadData() {
        checkIsLoggedIn()
    }

    private fun checkIsLoggedIn() {
        disposables.add(
            authenticationRepository.getIsAuthenticated()
                .zipWith(authenticationRepository.getIsLoggedIn()) { isAuthenticated, isLoggedIn ->
                    isAuthenticated to isLoggedIn
                }
                .applyScheduler()
                .subscribe { (isAuthenticated, isLoggedIn) ->
                    if (isAuthenticated)
                        loadViewerData()
                    else
                        isLoggedInSubject.onNext(isLoggedIn)
                }
        )
    }

    private fun loadViewerData() {
        disposables.add(
            authenticationRepository.viewer
                .applyScheduler()
                .subscribe {
                    isLoggedInSubject.onNext(it != User.EMPTY_USER)
                }
        )

        authenticationRepository.getViewerData()
    }
}