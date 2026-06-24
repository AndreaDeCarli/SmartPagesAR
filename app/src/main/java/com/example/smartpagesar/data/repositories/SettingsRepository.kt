package com.example.smartpagesar.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.smartpagesar.data.models.Theme
import com.google.ar.core.CameraConfig
import com.google.ar.core.Config
import kotlinx.coroutines.flow.map


class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object{
        private val THEME_KEY = stringPreferencesKey("theme")
        private val LIGHTING_KEY = stringPreferencesKey("lighting")
        private val FPS_KEY = stringPreferencesKey("fps")
    }

    val theme = dataStore.data.map { it[THEME_KEY]?: "System" }

    val lighting = dataStore.data.map { it[LIGHTING_KEY]?: "ENVIRONMENTAL_HDR" }

    val fps = dataStore.data.map { it[FPS_KEY]?: "TARGET_FPS_30" }

    suspend fun setTheme(theme: Theme) = dataStore.edit { it[THEME_KEY] = theme.toString() }

    suspend fun setLighting(lighting: Config.LightEstimationMode) = dataStore.edit { it[LIGHTING_KEY] = lighting.toString() }

    suspend fun setFps(fps: CameraConfig.TargetFps) = dataStore.edit { it[FPS_KEY] = fps.toString() }

}