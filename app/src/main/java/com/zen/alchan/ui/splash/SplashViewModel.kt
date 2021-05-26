package com.zen.alchan.ui.splash

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SplashViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _isLoggedIn = PublishSubject.create<Boolean>()
    val isLoggedIn: Observable<Boolean>
        get() = _isLoggedIn

    override fun loadData() {
        checkIsLoggedIn()
    }

    private fun checkIsLoggedIn() {
        disposables.add(
            userRepository.getIsAuthenticated()
                .zipWith(userRepository.getIsLoggedIn()) { isAuthenticated, isLoggedIn ->
                    isAuthenticated to isLoggedIn
                }
                .applyScheduler()
                .subscribe { (isAuthenticated, isLoggedIn) ->
                    if (isAuthenticated)
                        loadViewerData()
                    else
                        _isLoggedIn.onNext(isLoggedIn)
                }
        )
    }

    private fun loadViewerData() {
        disposables.add(
            userRepository.viewer
                .applyScheduler()
                .subscribe {
                    _isLoggedIn.onNext(it != User.EMPTY_USER)
                }
        )

        userRepository.loadViewer()
    }
}