package com.zen.alchan.ui.splash

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.extensions.isSessionExpired
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SplashViewModel(private val userRepository: UserRepository) : BaseViewModel<Unit>() {

    private val _isLoggedIn = PublishSubject.create<Boolean>()
    val isLoggedIn: Observable<Boolean>
        get() = _isLoggedIn

    private val _isSessionExpired = PublishSubject.create<Boolean>()
    val isSessionExpired: Observable<Boolean>
        get() = _isSessionExpired

    override fun loadData(param: Unit) {
        loadOnce {
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
            userRepository.getViewer(Source.NETWORK)
                .applyScheduler()
                .subscribe(
                    {
                        _isLoggedIn.onNext(true)
                    },
                    {
                        if (it.isSessionExpired()) {
                            _isSessionExpired.onNext(true)
                            _isLoggedIn.onNext(false)
                            return@subscribe
                        }

                        disposables.add(
                            userRepository.getViewer(Source.CACHE)
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
                )
        )
    }
}