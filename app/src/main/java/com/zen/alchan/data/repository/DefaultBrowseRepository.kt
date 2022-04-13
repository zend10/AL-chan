package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.BrowseDataSource
import com.zen.alchan.data.manager.BrowseManager
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.ListType
import io.reactivex.Observable
import type.StaffLanguage
import type.UserStatisticsSort

class DefaultBrowseRepository(
    private val browseDataSource: BrowseDataSource,
    private val browseManager: BrowseManager
) : BrowseRepository {

    private val userIdToUserMap = HashMap<Int, User>()

    override fun getUser(id: Int, sort: List<UserStatisticsSort>): Observable<User> {
        return if (userIdToUserMap.containsKey(id)) {
            Observable.just(userIdToUserMap[id])
        } else {
            browseDataSource.getUserQuery(id, sort).map {
                val newUser = it.data?.convert()
                if (newUser != null) {
                    userIdToUserMap[id] = newUser
                }
                newUser
            }
        }
    }

    override fun getOthersListType(): Observable<ListType> {
        return Observable.just(browseManager.othersListType)
    }

    override fun updateOthersListType(newListType: ListType) {
        browseManager.othersListType = newListType
    }

    override fun getMedia(id: Int): Observable<Media> {
        return browseDataSource.getMediaQuery(id).map {
            it.data?.convert()
        }
    }

    override fun getMediaCharacters(
        id: Int,
        page: Int,
        language: StaffLanguage
    ): Observable<Pair<PageInfo, List<CharacterEdge>>> {
        return browseDataSource.getMediaCharactersQuery(id, page, language).map {
            val characterConnection = it.data?.convert() ?: return@map Pair(PageInfo(), listOf())
            characterConnection.pageInfo to characterConnection.edges
        }
    }

    override fun getMediaStaff(id: Int, page: Int): Observable<Pair<PageInfo, List<StaffEdge>>> {
        return browseDataSource.getMediaStaffQuery(id, page).map {
            val staffConnection = it.data?.convert() ?: return@map Pair(PageInfo(), listOf())
            staffConnection.pageInfo to staffConnection.edges
        }
    }

    override fun getCharacter(id: Int): Observable<Character> {
        return browseDataSource.getCharacterQuery(id).map {
            it.data?.convert()
        }
    }

    override fun getStaff(id: Int): Observable<Staff> {
        return browseDataSource.getStaffQuery(id).map {
            it.data?.convert()
        }
    }
}