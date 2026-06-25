package com.example.smartpagesar.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpagesar.data.models.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val supabase: SupabaseClient
) : ViewModel() {

    var user by mutableStateOf<User?>(null)
        private set

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val userId = supabase.auth.currentSessionOrNull()?.user?.id
            if (userId != null) {
                user = supabase.from("Users").select{filter { eq("id", userId) }}.decodeSingle<User>()
            }
        }
    }
}



class ProfileViewModelFactory(
    private val supabase: SupabaseClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(supabase) as T
    }
}

