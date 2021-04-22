package com.zen.alchan.ui.login

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.NavigationManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class LoginViewModel(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    private val loginStatusSubject = PublishSubject.create<Boolean>()

    val loginStatus: Observable<Boolean>
        get() = loginStatusSubject

    fun login(bearerToken: String) {
        loadingSubject.onNext(true)
        authenticationRepository.saveBearerToken(bearerToken)

        disposables.add(
            authenticationRepository.getViewerQuery()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        loginStatusSubject.onNext(true)
                    },
                    {
                        errorSubject.onNext(it.sendMessage())
                    }
                )
        )
    }

    fun loginAsGuest() {
        authenticationRepository.loginAsGuest()
        loginStatusSubject.onNext(true)
    }
}