package com.zen.alchan.data.repository

import com.zen.alchan.data.response.User
import io.reactivex.Observable

interface AuthenticationRepository {
    fun getIsLoggedIn(): Observable<Boolean>
    fun getViewerQuery(): Observable<User>
    fun loginAsGuest()
}