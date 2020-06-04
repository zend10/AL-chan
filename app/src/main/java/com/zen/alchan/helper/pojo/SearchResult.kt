package com.zen.alchan.helper.pojo

class SearchResult(
    var animeSearchResult: SearchAnimeQuery.Medium? = null,
    var mangaSearchResult: SearchMangaQuery.Medium? = null,
    var charactersSearchResult: SearchCharactersQuery.Character? = null,
    var staffsSearchResult: SearchStaffsQuery.Staff? = null,
    var studiosSearchResult: SearchStudiosQuery.Studio? = null,
    var usersSearchResult: SearchUsersQuery.User? = null
)