package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.BrowseDataSource
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.User
import io.reactivex.Observable
import type.UserStatisticsSort

class DefaultBrowseRepository(private val browseDataSource: BrowseDataSource) : BrowseRepository {

    override fun getUser(id: Int, sort: List<UserStatisticsSort>): Observable<User> {
        return browseDataSource.getUserQuery(id, sort).map {
            it.data?.convert()
        }
    }

    override fun getMedia(id: Int): Observable<Media> {
        return browseDataSource.getMediaQuery(id).map {
            it.data?.convert()
        }
    }

    override fun getCharacter(id: Int): Observable<Character> {
        return browseDataSource.getCharacterQuery(id).map {
            it.data?.convert()
        }
    }
}