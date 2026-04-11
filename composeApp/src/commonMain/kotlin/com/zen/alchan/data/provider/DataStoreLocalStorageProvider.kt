package com.zen.alchan.data.provider

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zen.alchan.data.model.api.User
import com.zen.alchan.helper.LocalStorageConstant
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class DataStoreLocalStorageProvider(private val dataStore: DataStore<Preferences>?) :
    LocalStorageProvider {

    private val LANDING_COMPLETED = booleanPreferencesKey(LocalStorageConstant.LANDING_COMPLETED)
    private val ANILIST_TOKEN = stringPreferencesKey(LocalStorageConstant.ANILIST_TOKEN)
    private val CURRENT_ANILIST_USER =
        stringPreferencesKey(LocalStorageConstant.CURRENT_ANILIST_USER)

    override suspend fun getLandingCompleted(): Boolean {
        return dataStore?.data?.map { it[LANDING_COMPLETED] }?.firstOrNull() ?: false
    }

    override suspend fun setLandingCompleted() {
        dataStore?.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[LANDING_COMPLETED] = true
            }
        }
    }

    override suspend fun getAniListToken(): String {
        return dataStore?.data?.map { it[ANILIST_TOKEN] }?.firstOrNull() ?: ""
    }

    override suspend fun setAniListToken(token: String) {
        dataStore?.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[ANILIST_TOKEN] = token
            }
        }
    }

    override suspend fun getCurrentAniListUser(): User {
        val userString = dataStore?.data?.map { it[CURRENT_ANILIST_USER] }?.firstOrNull()
        return userString?.let { Json.decodeFromString<User>(it) } ?: User()
    }

    override suspend fun setCurrentAniListUser(user: User) {
        dataStore?.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[CURRENT_ANILIST_USER] = Json.encodeToString(user)
            }
        }
    }
}