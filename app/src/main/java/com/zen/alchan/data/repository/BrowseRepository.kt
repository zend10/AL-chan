package com.zen.alchan.data.repository

import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.ListType
import io.reactivex.Observable
import type.*

interface BrowseRepository {
    fun getUser(id: Int, sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC)): Observable<User>
    fun getOthersListType(): Observable<ListType>
    fun updateOthersListType(newListType: ListType)
    fun getMedia(id: Int): Observable<Media>
    fun getMediaCharacters(id: Int, page: Int, language: StaffLanguage): Observable<Pair<PageInfo, List<CharacterEdge>>>
    fun getMediaStaff(id: Int, page: Int): Observable<Pair<PageInfo, List<StaffEdge>>>
    fun getCharacter(id: Int, page: Int, sort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC), type: MediaType? = null, onList: Boolean? = null): Observable<Character>
    fun getStaff(
        id: Int,
        page: Int,
        staffMediaSort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC),
        characterSort: List<CharacterSort> = listOf(CharacterSort.FAVOURITES_DESC),
        characterMediaSort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC)
    ): Observable<Staff>
    fun getStudio(id: Int, page: Int, sort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC)): Observable<Studio>
}