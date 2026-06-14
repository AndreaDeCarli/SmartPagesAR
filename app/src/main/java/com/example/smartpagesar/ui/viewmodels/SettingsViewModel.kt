package com.example.smartpagesar.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpagesar.data.models.Theme
import com.example.smartpagesar.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsState(
    val theme: Theme
)

class SettingsViewModel(
    private val repository: SettingsRepository
): ViewModel() {
    var state by mutableStateOf(SettingsState(Theme.System))
        private set

    fun setTheme(theme: Theme){
        state = state.copy(theme = theme)
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }

    init {
        viewModelScope.launch {
            state = SettingsState(
                theme = Theme.valueOf(repository.theme.first())
            )
        }
    }
}