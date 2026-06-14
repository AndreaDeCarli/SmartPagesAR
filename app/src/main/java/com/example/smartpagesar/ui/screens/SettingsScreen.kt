package com.example.smartpagesar.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartpagesar.R
import com.example.smartpagesar.data.models.Theme
import com.example.smartpagesar.ui.composables.CustomDivider
import com.example.smartpagesar.ui.composables.MainTopAppBar
import com.example.smartpagesar.ui.viewmodels.SettingsState
import com.example.smartpagesar.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    state: SettingsState,
    settingsViewModel: SettingsViewModel
){
    Scaffold(
        topBar = { MainTopAppBar(
            navController = navController,
            title = stringResource(R.string.settings_title),
            goBack = true
        ) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                CustomDivider(stringResource(R.string.settings_title))
                Row(Modifier.selectableGroup()) {
                    Theme.entries.forEach { theme ->
                        Row(
                            Modifier
                                .height(56.dp)
                                .selectable(
                                    selected = (theme == state.theme),
                                    onClick = { settingsViewModel.setTheme(theme) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (theme == state.theme),
                                onClick = null
                            )
                            Text(
                                text = stringResource(theme.label),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}