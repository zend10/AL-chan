package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.BrowseDataSource
import com.zen.alchan.data.manager.BrowseManager
import com.zen.alchan.data.response.Anime
import com.zen.alchan.data.response.Manga
import com.zen.alchan.data.response.VideoSearch
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.CharacterEdge
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.PageInfo
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.data.response.anilist.StaffEdge
import com.zen.alchan.data.response.anilist.Studio
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.utils.AnimeThemesException
import com.zen.alchan.type.*
import io.reactivex.rxjava3.core.Observable

class DefaultBrowseRepository(
    private val browseDataSource: BrowseDataSource,
    private val browseManager: BrowseManager
) : BrowseRepository {

    private val userIdToUserMap = HashMap<Int, User>()

    override fun getUser(id: Int?, name: String?, sort: List<UserStatisticsSort>): Observable<User> {
        return if (userIdToUserMap.containsKey(id)) {
            Observable.just(userIdToUserMap[id] ?: User())
        } else {
            browseDataSource.getUserQuery(id, name, sort).map {
                val newUser = it.data?.convert()
                if (newUser != null) {
                    userIdToUserMap[newUser.id] = newUser
                }
                newUser ?: User()
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
            it.data?.convert() ?: Media()
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

    override fun getCharacter(id: Int, page: Int, sort: List<MediaSort>, type: MediaType?, onList: Boolean?): Observable<Character> {
        return browseDataSource.getCharacterQuery(id, page, sort, type, onList).map {
            it.data?.convert() ?: Character()
        }
    }

    override fun getStaff(
        id: Int,
        page: Int,
        staffMediaSort: List<MediaSort>,
        characterSort: List<CharacterSort>,
        characterMediaSort: List<MediaSort>,
        onList: Boolean?
    ): Observable<Staff> {
        return browseDataSource.getStaffQuery(id, page, staffMediaSort, characterSort, characterMediaSort, onList).map {
            it.data?.convert() ?: Staff()
        }
    }

    override fun getStudio(id: Int, page: Int, sort: List<MediaSort>, onList: Boolean?): Observable<Studio> {
        return browseDataSource.getStudioQuery(id, page, sort, onList).map {
            it.data?.convert() ?: Studio()
        }
    }

    override fun getMangaDetails(malId: Int): Observable<Manga> {
        return browseDataSource.getMangaDetails(malId).map {
            it.convert()
        }
    }

    override fun getAnimeDetails(malId: Int): Observable<Anime> {
        var getFromMal = false
        return Observable.just(true)
            .flatMap {
                if (!getFromMal) {
                    getAnimeDetailsFromAnimeThemes(malId)
                        .doOnError { getFromMal = true }
                        .map {
                            if (it.id == 0)
                                throw AnimeThemesException()
                            else
                                it
                        }
                } else {
                    getFromMal = false
                    getAnimeDetailsFromMal(malId)
                }
            }
            .retry { times, throwable ->
                if (throwable is AnimeThemesException) {
                    getFromMal = true
                    true
                } else {
                    false
                }
            }
    }

    private fun getAnimeDetailsFromAnimeThemes(malId: Int): Observable<Anime> {
        return browseDataSource.getAnimeDetailsFromAnimeThemes(malId).map {
            it.convert()
        }
    }

    private fun getAnimeDetailsFromMal(malId: Int): Observable<Anime> {
        return browseDataSource.getAnimeDetailsFromMal(malId).map {
            it.convert()
        }
    }

    override fun getYouTubeVideo(searchQuery: String): Observable<VideoSearch> {
        return browseDataSource.getYouTubeVideo(browseManager.youTubeApiKey, searchQuery).map {
            it.convert()
        }
    }
}