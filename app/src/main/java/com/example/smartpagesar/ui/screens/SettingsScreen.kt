package com.example.smartpagesar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.smartpagesar.data.models.Lighting
import com.example.smartpagesar.data.models.Theme
import com.example.smartpagesar.ui.composables.CustomDescription
import com.example.smartpagesar.ui.composables.CustomDivider
import com.example.smartpagesar.ui.composables.MainTopAppBar
import com.example.smartpagesar.ui.viewmodels.SettingsState
import com.example.smartpagesar.ui.viewmodels.SettingsViewModel
import com.google.ar.core.CameraConfig
import com.google.ar.core.Config

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
                CustomDivider(stringResource(R.string.theme_label))
                Row(Modifier.selectableGroup().fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
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
                CustomDescription(stringResource(R.string.tooltip_theme), MaterialTheme.colorScheme.onPrimaryContainer,12)
            }
            item {
                CustomDivider("Fps")
                Row (Modifier.selectableGroup().fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    CameraConfig.TargetFps.entries.forEach { fps ->
                        Row(
                            Modifier
                                .height(56.dp)
                                .selectable(
                                    selected = (fps == state.fps),
                                    onClick = { settingsViewModel.setFps(fps) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (fps == state.fps),
                                onClick = null
                            )
                            Text(
                                text = processString(fps.toString()),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
                CustomDescription(stringResource(R.string.tooltip_fps),MaterialTheme.colorScheme.onPrimaryContainer, 12)
            }
            item {
                CustomDivider("Lighting")
                Column(Modifier.selectableGroup().fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Config.LightEstimationMode.entries.forEach { lighting ->
                        Row(
                            Modifier
                                .height(56.dp)
                                .fillMaxWidth()
                                .selectable(
                                    selected = (lighting == state.lighting),
                                    onClick = { settingsViewModel.setLighting(lighting) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                selected = (lighting == state.lighting),
                                onClick = null
                            )
                            Text(
                                text = stringResource(Lighting.valueOf(lighting.toString()).label),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
                CustomDescription(stringResource(R.string.tooltip_lighting), MaterialTheme.colorScheme.onPrimaryContainer,12)
            }
        }
    }
}

fun processString(string: String): String{
    return string.replace("_", " ").lowercase().replaceFirstChar { char -> char.uppercase() }
}