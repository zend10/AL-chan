package com.zen.alchan.data.datasource

import ViewerQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.UserTitleLanguage

class UserDataSourceImpl(private val apolloHandler: ApolloHandler) : UserDataSource {

    override fun getViewerData(): Observable<Response<ViewerQuery.Data>> {
        val query = ViewerQuery.builder().build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        adultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<Response<AniListSettingsMutation.Data>> {
        val mutation = AniListSettingsMutation.builder()
            .titleLanguage(titleLanguage)
            .displayAdultContent(adultContent)
            .airingNotifications(airingNotifications)
            .build()
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}