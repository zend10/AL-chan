package com.zen.alchan.data.repository

import com.zen.alchan.data.model.api.Media

interface ContentRepository {
    suspend fun getTrending(): List<Media>
}