package com.example.smartpagesar

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.smartpagesar.data.repositories.SettingsRepository
import com.example.smartpagesar.ui.viewmodels.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single { SettingsRepository(get()) }

    viewModel { SettingsViewModel(get()) }
}