package com.zen.alchan.data.localstorage

import android.content.Context
import android.net.Uri
import com.zen.alchan.helper.pojo.NullableItem
import io.reactivex.rxjava3.core.Observable

class DefaultFileStorageHandler(context: Context) : FileStorageHandler, BaseFileStorageHandler(context) {

    override val animeListBackground: Observable<NullableItem<Uri>>
        get() = getUri(ANIME_FILE_BACKGROUND_FILENAME)

    override val mangaListBackground: Observable<NullableItem<Uri>>
        get() = getUri(MANGA_FILE_BACKGROUND_FILENAME)

    override fun saveAnimeListBackground(uri: Uri?): Observable<Unit> {
        return saveUri(uri, ANIME_FILE_BACKGROUND_FILENAME)
    }

    override fun saveMangaListBackground(uri: Uri?): Observable<Unit> {
        return saveUri(uri, MANGA_FILE_BACKGROUND_FILENAME)
    }

    companion object {
        private const val ANIME_FILE_BACKGROUND_FILENAME = "anime_list_background.jpg"
        private const val MANGA_FILE_BACKGROUND_FILENAME = "manga_list_background.jpg"
    }
}