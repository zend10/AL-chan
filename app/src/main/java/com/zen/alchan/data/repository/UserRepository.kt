package com.zen.alchan.data.repository

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.helper.enums.Source
import io.reactivex.Observable
import type.UserStatisticsSort

interface UserRepository {

    fun getProfileData(
        userId: Int,
        sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC),
        source: Source?
    ): Observable<ProfileData>

    fun getAppSetting(): Observable<AppSetting>
}