package com.zen.alchan.ui.main

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel

class MainViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {

    override fun loadData() {
        checkIsAuthenticated()
    }

    private fun checkIsAuthenticated() {
        disposables.add(
            userRepository.getIsAuthenticated()
                .applyScheduler()
                .subscribe {
                    _isAuthenticated.onNext(it)
                }
        )
    }
}