package com.zen.alchan.data.repository

import android.net.Uri
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.response.anilist.MediaListTypeOptions
import com.zen.alchan.data.response.anilist.NotificationOption
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.response.anilist.PageInfo
import com.zen.alchan.helper.pojo.NullableItem
import io.reactivex.Observable
import type.ScoreFormat
import type.UserStaffNameLanguage
import type.UserStatisticsSort
import type.UserTitleLanguage

interface UserRepository {

    val refreshMainScreenTrigger: Observable<Unit>

    fun getIsLoggedInAsGuest(): Observable<Boolean>
    fun getIsAuthenticated(): Observable<Boolean>
    fun getViewer(
        source: Source? = null,
        sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC)
    ): Observable<User>
    fun loginAsGuest()
    fun logoutAsGuest()
    fun logout()
    fun saveBearerToken(newBearerToken: String?)

    fun getFollowingAndFollowersCount(
        userId: Int,
        source: Source? = null
    ): Observable<Pair<Int, Int>>

    fun getFollowing(userId: Int, page: Int): Observable<Pair<PageInfo, List<User>>>
    fun getFollowers(userId: Int, page: Int): Observable<Pair<PageInfo, List<User>>>

    fun getListStyle(mediaType: MediaType): Observable<ListStyle>
    fun setListStyle(mediaType: MediaType, newListStyle: ListStyle)
    fun getListBackground(mediaType: MediaType): Observable<NullableItem<Uri>>
    fun setListBackground(mediaType: MediaType, newUri: Uri?): Observable<Unit>

    fun getMediaFilter(mediaType: MediaType): Observable<MediaFilter>
    fun setMediaFilter(mediaType: MediaType, newMediaFilter: MediaFilter)

    fun getAppSetting(): Observable<AppSetting>
    fun setAppSetting(newAppSetting: AppSetting?): Observable<Unit>

    fun getAppTheme(): AppTheme

    fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        staffNameLanguage: UserStaffNameLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<User>

    fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ): Observable<User>

    fun updateNotificationsSettings(
        notificationOptions: List<NotificationOption>
    ): Observable<User>
}