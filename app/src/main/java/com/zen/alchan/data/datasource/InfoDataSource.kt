package com.zen.alchan.data.datasource

import com.zen.alchan.data.response.github.AnnouncementResponse
import io.reactivex.rxjava3.core.Observable

interface InfoDataSource {
    fun getAnnouncement(): Observable<AnnouncementResponse>
}