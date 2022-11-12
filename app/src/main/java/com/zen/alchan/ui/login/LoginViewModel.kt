package com.zen.alchan.ui.login

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class LoginViewModel(private val userRepository: UserRepository) : BaseViewModel<Unit>() {

    private val _loginTrigger = PublishSubject.create<Unit>()
    val loginTrigger: Observable<Unit>
        get() = _loginTrigger

    override fun loadData(param: Unit) = Unit

    fun login(bearerToken: String) {
        _loading.onNext(true)
        userRepository.saveBearerToken(bearerToken)

        disposables.add(
            userRepository.getViewer(Source.NETWORK)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        _loginTrigger.onNext(Unit)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun loginAsGuest() {
        userRepository.loginAsGuest()
        _loginTrigger.onNext(Unit)
    }
}