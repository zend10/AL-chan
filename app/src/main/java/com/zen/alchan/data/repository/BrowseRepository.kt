package com.zen.alchan.data.repository

import com.zen.alchan.data.response.anilist.User
import io.reactivex.Observable
import type.UserStatisticsSort

interface BrowseRepository {
    fun getUser(id: Int, sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC)): Observable<User>
}