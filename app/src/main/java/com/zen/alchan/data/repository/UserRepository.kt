package com.zen.alchan.data.repository

import android.net.Uri
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.Favorite
import com.zen.alchan.helper.pojo.NullableItem
import io.reactivex.Observable
import type.ScoreFormat
import type.UserStaffNameLanguage
import type.UserStatisticsSort
import type.UserTitleLanguage

interface UserRepository {

    val refreshMainScreenTrigger: Observable<Unit>
    val refreshFavoriteTrigger: Observable<User>

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
    fun toggleFollow(userId: Int): Observable<Boolean>

    fun getUserStatistics(userId: Int, sort: UserStatisticsSort): Observable<UserStatisticTypes>
    fun getFavorites(userId: Int, page: Int): Observable<Favourites>
    fun updateFavoriteOrder(ids: List<Int>, favorite: Favorite): Observable<Favourites>

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