package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource

interface AuthRepository {
    val appColorTheme: Int
    val appColorThemeLiveData: LiveData<Int>
    val viewerDataResponse: LiveData<Resource<Boolean>>

    val isLoggedIn: Boolean

    fun setBearerToken(accessToken: String)
    fun retrieveViewerData()
}