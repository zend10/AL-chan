package com.zen.alchan.data.repository

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.Source
import io.reactivex.Observable
import type.UserStatisticsSort

interface UserRepository {

    val viewer: Observable<User>
    val appSetting: Observable<AppSetting>

    fun getIsLoggedIn(): Observable<Boolean>
    fun getIsAuthenticated(): Observable<Boolean>
    fun loadViewer(source: Source? = null)
    fun loginAsGuest()
    fun logout()
    fun saveBearerToken(newBearerToken: String?)

    fun getProfileData(
        userId: Int,
        sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC),
        source: Source?
    ): Observable<ProfileData>

    fun loadAppSetting()
    fun setAppSetting(newAppSetting: AppSetting?)

    fun getAppTheme(): AppTheme
}