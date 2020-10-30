package com.zen.alchan.data.localstorage

class TempStorageManagerImpl : TempStorageManager {
    override var youtubeKey: String? = null
    override var spotifyKey: String? = null
    override var spotifyAccessTokenLastRetrieve: Long? = null
    override var spotifyAccessTokenExpiresIn: Int? = null
    override var spotifyAccessToken: String? = null
}