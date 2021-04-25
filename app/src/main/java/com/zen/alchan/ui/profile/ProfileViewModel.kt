package com.zen.alchan.ui.profile

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.response.User
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ProfileViewModel(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    private val userDataSubject = BehaviorSubject.createDefault(User.EMPTY_USER)

    val userData: Observable<User>
        get() = userDataSubject

    var userId = 0

    fun checkIsAuthenticated() {
        disposables.add(
            authenticationRepository.getIsAuthenticated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    loadUserData()
                    isAuthenticatedSubject.onNext(it)
                }
        )
    }

    private fun loadUserData() {
        if (state == State.LOADED) return

        disposables.add(
            authenticationRepository.viewer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    userDataSubject.onNext(it)
                    state = State.LOADED
                }
        )

        if (userId == 0)
            authenticationRepository.getViewerData()


    }

    fun logoutAsGuest() {
        authenticationRepository.loginAsGuest(false)
    }
}