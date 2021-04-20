package com.zen.alchan.ui.login

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class LoginViewModel(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    private val loadingSubject = BehaviorSubject.createDefault(false)
    private val authenticateStatusSubject = PublishSubject.create<Unit>()

    val loading: Observable<Boolean>
        get() = loadingSubject

    val authenticateStatus: Observable<Unit>
        get() = authenticateStatusSubject

    fun loginAsGuest() {
        authenticationRepository.loginAsGuest()
    }

    fun login(bearerToken: String) {
        loadingSubject.onNext(true)
    }
}