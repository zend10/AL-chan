package com.zen.alchan.data.repository

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.pojo.ListStyle
import io.reactivex.Observable
import type.MediaType
import type.UserStatisticsSort

interface UserRepository {

    val viewerAndAppSetting: Observable<Pair<User, AppSetting>>

    fun getIsLoggedIn(): Observable<Boolean>
    fun getIsAuthenticated(): Observable<Boolean>
    fun loadViewerAndAppSetting()
    fun getViewer(source: Source? = null): Observable<User>
    fun loginAsGuest()
    fun logout()
    fun saveBearerToken(newBearerToken: String?)

    fun getProfileData(
        userId: Int,
        sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC),
        source: Source?
    ): Observable<ProfileData>

    fun getListStyle(mediaType: MediaType): Observable<ListStyle>
    fun setListStyle(mediaType: MediaType, newListStyle: ListStyle)

    fun getAppSetting(): Observable<AppSetting>
    fun setAppSetting(newAppSetting: AppSetting?)

    fun getAppTheme(): AppTheme
}