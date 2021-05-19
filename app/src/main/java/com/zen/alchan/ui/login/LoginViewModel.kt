package com.zen.alchan.ui.login

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class LoginViewModel(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    private val _loginStatus = PublishSubject.create<Boolean>()
    val loginStatus: Observable<Boolean>
        get() = _loginStatus

    override fun loadData() {
        // do nothing
    }

    fun login(bearerToken: String) {
        _loading.onNext(true)
        authenticationRepository.saveBearerToken(bearerToken)

        disposables.add(
            authenticationRepository.viewer
                .applyScheduler()
                .subscribe{
                    if (it != User.EMPTY_USER)
                        _loginStatus.onNext(true)
                    else
                        _error.onNext(0)
                }
        )

        authenticationRepository.getViewerData()
    }

    fun loginAsGuest() {
        authenticationRepository.loginAsGuest(true)
        _loginStatus.onNext(true)
    }
}