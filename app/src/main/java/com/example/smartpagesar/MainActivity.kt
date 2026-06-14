package com.example.smartpagesar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.compose.rememberNavController
import com.example.smartpagesar.data.models.Theme
import com.example.smartpagesar.ui.SmartPagesARNavGraph
import com.example.smartpagesar.ui.theme.SmartPagesARTheme
import com.example.smartpagesar.ui.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val settingsViewModel = koinViewModel<SettingsViewModel>()
            val settingsState = settingsViewModel.state

            SmartPagesARTheme (
                darkTheme = when(settingsState.theme){
                    Theme.Dark -> true
                    Theme.Light -> false
                    Theme.System -> isSystemInDarkTheme()
                },
                dynamicColor = false
            ){
                val navController = rememberNavController()
                SmartPagesARNavGraph(navController, settingsState, settingsViewModel)
            }
        }
    }
}
