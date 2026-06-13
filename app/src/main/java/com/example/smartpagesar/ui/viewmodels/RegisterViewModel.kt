package com.example.smartpagesar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val supabase: SupabaseClient
) : ViewModel() {

    data class UiState(
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value

            if (state.password != state.confirmPassword) {
                _uiState.update { it.copy(error = "Passwords do not match") }
                return@launch
            }

            try {
                supabase.auth.signUpWith(Email) {
                    email = state.email
                    password = state.password
                }

                _uiState.update { it.copy(error = null) }
                onSuccess()

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
