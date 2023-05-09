package com.zen.alchan.data.repository

import com.zen.alchan.data.response.Manga
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.CharacterEdge
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.PageInfo
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.data.response.anilist.StaffEdge
import com.zen.alchan.data.response.anilist.Studio
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.type.*
import io.reactivex.rxjava3.core.Observable

interface BrowseRepository {
    fun getUser(id: Int? = null, name: String? = null, sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC)): Observable<User>
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
        characterMediaSort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC),
        onList: Boolean? = null
    ): Observable<Staff>
    fun getStudio(id: Int, page: Int, sort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC), onList: Boolean? = null): Observable<Studio>

    fun getMangaDetails(malId: Int): Observable<Manga>
}