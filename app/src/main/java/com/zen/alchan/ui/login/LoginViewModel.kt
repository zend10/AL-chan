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

    private val loadingSubject = BehaviorSubject.createDefault(false)
    private val errorSubject = PublishSubject.create<Int>()
    private val webUrlSubject = PublishSubject.create<NavigationManager.Url>()
    private val backNavigationSubject = PublishSubject.create<Unit>()

    val loading: Observable<Boolean>
        get() = loadingSubject

    val error: Observable<Int>
        get() = errorSubject

    val webUrl: Observable<NavigationManager.Url>
        get() = webUrlSubject

    val backNavigation: Observable<Unit>
        get() = backNavigationSubject

    fun login(bearerToken: String) {
        loadingSubject.onNext(true)
        authenticationRepository.saveBearerToken(bearerToken)

        disposables.add(
            authenticationRepository.getViewerQuery()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        navigationSubject.onNext(NavigationManager.Page.MAIN to listOf())
                    },
                    {
                        errorSubject.onNext(it.sendMessage())
                    }
                )
        )
    }

    fun pressEnterWithoutLogin() {
        authenticationRepository.loginAsGuest()
        navigationSubject.onNext(NavigationManager.Page.MAIN to listOf())
    }

    fun pressRegister() {
        webUrlSubject.onNext(NavigationManager.Url.ANILIST_REGISTER)
    }

    fun pressLogin() {
        webUrlSubject.onNext(NavigationManager.Url.ANILIST_LOGIN)
    }

    fun pressAniListLink() {
        webUrlSubject.onNext(NavigationManager.Url.ANILIST_WEBSITE)
    }

    fun pressBack() {
        backNavigationSubject.onNext(Unit)
    }
}