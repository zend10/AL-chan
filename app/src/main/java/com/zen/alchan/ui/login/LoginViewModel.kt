package com.zen.alchan.ui.login

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class LoginViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _loginStatus = PublishSubject.create<Unit>()
    val loginStatus: Observable<Unit>
        get() = _loginStatus

    override fun loadData() {
        // do nothing
    }

    fun login(bearerToken: String) {
        _loading.onNext(true)
        userRepository.saveBearerToken(bearerToken)

        disposables.add(
            userRepository.viewer
                .applyScheduler()
                .subscribe{
                    if (it != User.EMPTY_USER)
                        _loginStatus.onNext(Unit)
                    else
                        _error.onNext(0)
                }
        )

        userRepository.loadViewer()
    }

    fun loginAsGuest() {
        userRepository.loginAsGuest()
        _loginStatus.onNext(Unit)
    }
}