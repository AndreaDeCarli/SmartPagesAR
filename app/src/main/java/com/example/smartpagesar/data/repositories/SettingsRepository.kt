package com.example.smartpagesar.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.smartpagesar.data.models.Theme
import kotlinx.coroutines.flow.map


class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object{
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val theme = dataStore.data.map { it[THEME_KEY]?: "System" }

    suspend fun setTheme(theme: Theme) = dataStore.edit { it[THEME_KEY] = theme.toString() }

}