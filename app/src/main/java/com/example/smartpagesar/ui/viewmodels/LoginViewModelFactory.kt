package com.example.smartpagesar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.jan.supabase.SupabaseClient

class LoginViewModelFactory(
    private val supabase: SupabaseClient
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(supabase) as T
    }
}