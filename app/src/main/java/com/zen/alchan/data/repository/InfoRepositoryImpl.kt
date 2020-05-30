package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.datasource.InfoDataSource
import com.zen.alchan.data.localstorage.InfoManager
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.utils.AndroidUtility

class InfoRepositoryImpl(private val infoDataSource: InfoDataSource,
                         private val infoManager: InfoManager) : InfoRepository {

    private val _announcementResponse = SingleLiveEvent<Resource<Announcement>>()
    override val announcementResponse: LiveData<Resource<Announcement>>
        get() = _announcementResponse

    override val lastAnnouncementId: Int?
        get() = infoManager.lastAnnouncementId

    override fun getAnnouncement() {
        infoDataSource.getAnnouncement().enqueue(AndroidUtility.apiCallback(_announcementResponse))
    }

    override fun setLastAnnouncementId(value: Int) {
        infoManager.setLastAnnouncementId(value)
    }
}