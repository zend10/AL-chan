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
        load {
            checkIsLoggedIn()
        }
    }

    private fun checkIsLoggedIn() {
        disposables.add(
            Observable.zip(
                userRepository.getIsAuthenticated(),
                userRepository.getIsLoggedInAsGuest()
            ) { isAuthenticated, isLoggedInAsGuest ->
                isAuthenticated to isLoggedInAsGuest
            }
                .applyScheduler()
                .subscribe { (isAuthenticated, isLoggedInAsGuest) ->
                    if (isAuthenticated) {
                        loadViewerData()
                    } else {
                        _isLoggedIn.onNext(isLoggedInAsGuest)
                    }
                }
        )
    }

    private fun loadViewerData() {
        disposables.add(
            userRepository.getViewer()
                .applyScheduler()
                .subscribe(
                    {
                        _isLoggedIn.onNext(true)
                    },
                    {
                        _isLoggedIn.onNext(false)
                    }
                )
        )
    }
}