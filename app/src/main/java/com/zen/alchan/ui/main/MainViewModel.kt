package com.zen.alchan.ui.main

import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel(
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {

    fun getViewerData() {

    }

    fun checkIsAuthenticated() {
        disposables.add(
            authenticationRepository.getIsAuthenticated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isAuthenticatedSubject.onNext(it)
                }
        )
    }
}