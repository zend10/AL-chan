package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.moreThanADay
import com.zen.alchan.helper.pojo.SaveItem
import com.zen.alchan.helper.utils.StorageException
import io.reactivex.Observable
import type.UserStatisticsSort

class DefaultUserRepository(private val userDataSource: UserDataSource, private val userManager: UserManager) : UserRepository {

    override fun getProfileData(userId: Int, sort: List<UserStatisticsSort>, source: Source?): Observable<ProfileData> {
        return when (source) {
            Source.NETWORK -> getProfileDataFromNetwork(userId, sort)
            Source.CACHE -> getProfileDataFromCache()
            else -> {
                val savedItem = userManager.profileData
                if (savedItem == null || savedItem.saveTime.moreThanADay()) {
                    getProfileDataFromNetwork(userId, sort)
                } else {
                    Observable.just(savedItem.data)
                }
            }
        }
    }

    private fun getProfileDataFromNetwork(userId: Int, sort: List<UserStatisticsSort>): Observable<ProfileData> {
        return userDataSource.getProfileQuery(userId, sort).map {
            val newProfileData = it.data?.convert() ?: ProfileData()
            userManager.profileData = SaveItem(newProfileData)
            newProfileData
        }
    }

    private fun getProfileDataFromCache(): Observable<ProfileData> {
        val savedItem = userManager.profileData?.data
        return if (savedItem != null) Observable.just(savedItem) else Observable.error(StorageException())
    }
}