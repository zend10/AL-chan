package com.zen.alchan.ui.main

import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel(
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {

    override fun loadData() {
        checkIsAuthenticated()
    }

    private fun checkIsAuthenticated() {
        disposables.add(
            authenticationRepository.getIsAuthenticated()
                .applyScheduler()
                .subscribe {
                    _isAuthenticated.onNext(it)
                }
        )
    }
}