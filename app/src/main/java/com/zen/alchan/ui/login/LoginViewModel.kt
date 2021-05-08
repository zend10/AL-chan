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

    private val loginStatusSubject = PublishSubject.create<Boolean>()

    val loginStatus: Observable<Boolean>
        get() = loginStatusSubject

    override fun loadData() {
        // do nothing
    }

    fun login(bearerToken: String) {
        loadingSubject.onNext(true)
        authenticationRepository.saveBearerToken(bearerToken)

        disposables.add(
            authenticationRepository.viewer
                .applyScheduler()
                .subscribe{
                    if (it != User.EMPTY_USER)
                        loginStatusSubject.onNext(true)
                    else
                        errorSubject.onNext(0)
                }
        )

        authenticationRepository.getViewerData()
    }

    fun loginAsGuest() {
        authenticationRepository.loginAsGuest(true)
        loginStatusSubject.onNext(true)
    }
}