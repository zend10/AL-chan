package com.zen.alchan.data.datasource

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.rx3.rxSingle
import com.zen.alchan.*
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.data.network.retrofit.RetrofitHandler
import com.zen.alchan.data.response.mal.MangaResponse
import com.zen.alchan.type.*
import io.reactivex.rxjava3.core.Observable

class DefaultBrowseDataSource(
    private val apolloHandler: ApolloHandler,
    private val retrofitHandler: RetrofitHandler,
    private val statusVersion: Int,
    private val sourceVersion: Int,
    private val relationTypeVersion: Int
) : BrowseDataSource {

    override fun getUserQuery(
        id: Int?,
        name:  String?,
        sort: List<UserStatisticsSort>
    ): Observable<ApolloResponse<UserQuery.Data>> {
        val query = UserQuery(id = Optional.present(id), name = Optional.presentIfNotNull(name), sort = Optional.present(sort))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getMediaQuery(id: Int): Observable<ApolloResponse<MediaQuery.Data>> {
        val query = MediaQuery(
            id = Optional.present(id),
            statusVersion = Optional.present(statusVersion),
            sourceVersion = Optional.present(sourceVersion),
            relationTypeVersion = Optional.present(relationTypeVersion)
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getMediaCharactersQuery(
        id: Int,
        page: Int,
        language: StaffLanguage
    ): Observable<ApolloResponse<MediaCharactersQuery.Data>> {
        val query = MediaCharactersQuery(id = Optional.present(id), page = Optional.present(page), language = Optional.present(language))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getMediaStaffQuery(
        id: Int,
        page: Int
    ): Observable<ApolloResponse<MediaStaffQuery.Data>> {
        val query = MediaStaffQuery(id = Optional.present(id), page = Optional.present(page))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getCharacterQuery(id: Int, page: Int, sort: List<MediaSort>, type: MediaType?, onList: Boolean?): Observable<ApolloResponse<CharacterQuery.Data>> {
        val query = CharacterQuery(id = Optional.present(id), page = Optional.present(page), sort = Optional.presentIfNotNull(sort), type = Optional.presentIfNotNull(type), onList = Optional.presentIfNotNull(onList))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getStaffQuery(
        id: Int,
        page: Int,
        staffMediaSort: List<MediaSort>,
        characterSort: List<CharacterSort>,
        characterMediaSort: List<MediaSort>,
        onList: Boolean?
    ): Observable<ApolloResponse<StaffQuery.Data>> {
        val query = StaffQuery(
            id = Optional.present(id),
            page = Optional.present(page),
            staffMediaSort = Optional.presentIfNotNull(staffMediaSort),
            characterSort = Optional.presentIfNotNull(characterSort),
            characterMediaSort = Optional.presentIfNotNull(characterMediaSort),
            onList = Optional.presentIfNotNull(onList)
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getStudioQuery(
        id: Int,
        page: Int,
        sort: List<MediaSort>,
        onList: Boolean?
    ): Observable<ApolloResponse<StudioQuery.Data>> {
        val query = StudioQuery(
            id = Optional.present(id),
            page = Optional.present(page),
            sort = Optional.presentIfNotNull(sort),
            onList = Optional.presentIfNotNull(onList)
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getMangaDetails(malId: Int): Observable<MangaResponse> {
        return retrofitHandler.jikanRetrofitClient().getMangaDetails(malId)
    }
}