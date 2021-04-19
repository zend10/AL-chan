package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.AuthenticationDataSource
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.response.User
import io.reactivex.Observable

class DefaultAuthenticationRepository(
    private val authenticationDataSource: AuthenticationDataSource,
    private val userManager: UserManager
) : AuthenticationRepository {

    override fun getViewerQuery(): Observable<User> {
        return authenticationDataSource.getViewerQuery().map { it.data?.convert() ?: User() }
    }
}