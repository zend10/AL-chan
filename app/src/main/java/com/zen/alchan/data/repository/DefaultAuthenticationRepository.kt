package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.AuthenticationDataSource
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class DefaultAuthenticationRepository(
    private val authenticationDataSource: AuthenticationDataSource,
    private val userManager: UserManager
) : AuthenticationRepository {

    private val viewerSubject = PublishSubject.create<User>()

    override val viewer: Observable<User>
        get() = viewerSubject

    private var viewerDisposable: Disposable? = null

    override fun getIsLoggedIn(): Observable<Boolean> {
        return Observable.just(userManager.isAuthenticated || userManager.isLoggedInAsGuest)
    }

    override fun getIsAuthenticated(): Observable<Boolean> {
        return Observable.just(userManager.isAuthenticated)
    }

    override fun getViewerData() {
        viewerDisposable = authenticationDataSource.getViewerQuery()
            .subscribe(
                {
                    it.data?.let { user ->
                        val newViewerData = user.convert()
                        userManager.viewerData = SaveItem(newViewerData)
                        viewerSubject.onNext(newViewerData)
                    }
                    viewerDisposable = null
                },
                {
                    val savedItem = userManager.viewerData?.data
                    if (savedItem != null) {
                        viewerSubject.onNext(savedItem)
                    } else {
                        viewerSubject.onNext(User.EMPTY_USER)
                    }
                    viewerDisposable = null
                }
            )
    }

    override fun getViewerDataFromCache(): Observable<User> {
        return Observable.just(userManager.viewerData?.data)
    }

    override fun loginAsGuest(shouldLogin: Boolean) {
        userManager.isLoggedInAsGuest = shouldLogin
    }

    override fun saveBearerToken(newBearerToken: String?) {
        userManager.bearerToken = newBearerToken
    }
}