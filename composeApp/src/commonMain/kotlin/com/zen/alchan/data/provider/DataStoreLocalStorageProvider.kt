package com.zen.alchan.data.provider

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.zen.alchan.helper.LocalStorageConstant
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class DataStoreLocalStorageProvider(private val dataStore: DataStore<Preferences>?) :
    LocalStorageProvider {

    private val LANDING_COMPLETED = booleanPreferencesKey(LocalStorageConstant.LANDING_COMPLETED)

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
}