package com.zen.alchan.data.repository

import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.ListType
import io.reactivex.Observable
import type.StaffLanguage
import type.UserStatisticsSort

interface BrowseRepository {
    fun getUser(id: Int, sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC)): Observable<User>
    fun getOthersListType(): Observable<ListType>
    fun updateOthersListType(newListType: ListType)
    fun getMedia(id: Int): Observable<Media>
    fun getMediaCharacters(id: Int, page: Int, language: StaffLanguage): Observable<Pair<PageInfo, List<CharacterEdge>>>
    fun getCharacter(id: Int): Observable<Character>
    fun getStaff(id: Int): Observable<Staff>
}