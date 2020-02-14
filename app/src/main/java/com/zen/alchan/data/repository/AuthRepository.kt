package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource

interface AuthRepository {
    val appColorTheme: Int
    val viewerDataResponse: LiveData<Resource<ViewerQuery.Data>>

    val isLoggedIn: Boolean
    val shouldRetrieveViewerData: Boolean

    fun setBearerToken(accessToken: String)
    fun getViewerData()
}