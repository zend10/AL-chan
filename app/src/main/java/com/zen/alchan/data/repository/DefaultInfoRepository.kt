package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.InfoDataSource
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.data.response.github.AnnouncementResponse
import io.reactivex.Observable

class DefaultInfoRepository(private val infoDataSource: InfoDataSource, private val userManager: UserManager) : InfoRepository {

    override fun getAnnouncement(): Observable<Announcement> {
        return infoDataSource.getAnnouncement().map {
            it.convert()
        }
    }

    override fun getLastAnnouncementId(): Observable<String> {
        return Observable.just(userManager.lastAnnouncementId ?: "")
    }

    override fun setLastAnnouncementId(announcementId: String) {
        userManager.lastAnnouncementId = announcementId
    }
}