package com.example.pixelnothingwidgets.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val CAIYUN_TOKEN_KEY = stringPreferencesKey("caiyun_token")
        private val LATITUDE_KEY = stringPreferencesKey("latitude")
        private val LONGITUDE_KEY = stringPreferencesKey("longitude")
        private val CITY_NAME_KEY = stringPreferencesKey("city_name")
    }

    val caiyunToken: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CAIYUN_TOKEN_KEY] ?: ""
        }

    val latitude: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LATITUDE_KEY] ?: ""
        }

    val longitude: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LONGITUDE_KEY] ?: ""
        }

    val cityName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CITY_NAME_KEY] ?: ""
        }

    suspend fun saveSettings(
        token: String,
        latitude: String,
        longitude: String,
        cityName: String
    ) {
        context.dataStore.edit {
            it[CAIYUN_TOKEN_KEY] = token
            it[LATITUDE_KEY] = latitude
            it[LONGITUDE_KEY] = longitude
            it[CITY_NAME_KEY] = cityName
        }
    }

    suspend fun clearSettings() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
