package com.zen.alchan.ui.splash

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.NavigationManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class SplashViewModel(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    fun checkIsLoggedIn() {
        disposables.add(
            authenticationRepository.getIsLoggedIn()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it)
                        navigationSubject.onNext(NavigationManager.Page.MAIN to listOf())
                    else
                        navigationSubject.onNext(NavigationManager.Page.LANDING to listOf())
                }
        )
    }
}