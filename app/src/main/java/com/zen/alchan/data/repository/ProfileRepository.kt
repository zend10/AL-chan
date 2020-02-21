package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.User

interface ProfileRepository {
    val viewerDataResponse: LiveData<Resource<Boolean>>
    val viewerData: LiveData<User?>
    fun getViewerData()
    fun retrieveViewerData()
}