package com.example.smartpagesar.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpagesar.data.models.Theme
import com.example.smartpagesar.data.repositories.SettingsRepository
import com.google.ar.core.CameraConfig
import com.google.ar.core.Config
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsState(
    val theme: Theme,
    val lighting: Config.LightEstimationMode,
    val fps: CameraConfig.TargetFps
)

class SettingsViewModel(
    private val repository: SettingsRepository
): ViewModel() {
    var state by mutableStateOf(SettingsState(Theme.System, Config.LightEstimationMode.ENVIRONMENTAL_HDR ,
        CameraConfig.TargetFps.TARGET_FPS_30 ))
        private set

    fun setTheme(theme: Theme){
        state = state.copy(theme = theme)
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }

    fun setLighting(lighting: Config.LightEstimationMode){
        state = state.copy(lighting = lighting)
        viewModelScope.launch {
            repository.setLighting(lighting = lighting)
        }
    }

    fun setFps(fps: CameraConfig.TargetFps){
        state = state.copy(fps = fps)
        viewModelScope.launch {
            repository.setFps(fps = fps)
        }
    }

    init {
        viewModelScope.launch {
            state = SettingsState(
                theme = Theme.valueOf(repository.theme.first()),
                lighting = Config.LightEstimationMode.valueOf(repository.lighting.first()),
                fps = CameraConfig.TargetFps.valueOf(repository.fps.first())
            )
        }
    }
}