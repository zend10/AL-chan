package com.zen.alchan.data.repository

import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.Source
import io.reactivex.Observable

interface AuthenticationRepository {
    val viewer: Observable<User>
    fun getIsLoggedIn(): Observable<Boolean>
    fun getIsAuthenticated(): Observable<Boolean>
    fun getViewerData()
    fun loginAsGuest(shouldLogin: Boolean)
    fun saveBearerToken(newBearerToken: String?)
}