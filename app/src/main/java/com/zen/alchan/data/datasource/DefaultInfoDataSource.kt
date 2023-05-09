package com.zen.alchan.data.datasource

import com.zen.alchan.data.network.retrofit.RetrofitHandler
import com.zen.alchan.data.response.github.AnnouncementResponse
import io.reactivex.rxjava3.core.Observable

class DefaultInfoDataSource(private val retrofitHandler: RetrofitHandler) : InfoDataSource {

    override fun getAnnouncement(): Observable<AnnouncementResponse> {
        return retrofitHandler.gitHubRetrofitClient().getAnnouncement()
    }
}