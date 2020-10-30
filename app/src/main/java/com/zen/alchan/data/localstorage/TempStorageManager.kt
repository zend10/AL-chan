package com.zen.alchan.data.localstorage

interface TempStorageManager {
    var youtubeKey: String?
    var spotifyKey: String?
    var spotifyAccessTokenLastRetrieve: Long?
    var spotifyAccessTokenExpiresIn: Int?
    var spotifyAccessToken: String?
}