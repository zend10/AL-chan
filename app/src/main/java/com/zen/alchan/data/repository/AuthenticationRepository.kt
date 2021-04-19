package com.zen.alchan.data.repository

import com.zen.alchan.data.response.User
import io.reactivex.Observable

interface AuthenticationRepository {
    fun getViewerQuery(): Observable<User>
}