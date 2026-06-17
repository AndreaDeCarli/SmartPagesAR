package com.example.smartpagesar.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.smartpagesar.ui.composables.MainBottomAppBar
import com.example.smartpagesar.ui.viewmodels.ARScreenViewModel
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARSceneView // Ensure correct import for 4.X Composable

@Composable
fun ARScreen(
    navController: NavController,
    viewModel: ARScreenViewModel
) {
    val context = LocalContext.current
    val recognized by viewModel.recognizedImage.collectAsState()

    // 1. Camera Permission Check
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Consenti la fotocamera per usare la realtà aumentata")
        }
        return
    }
    Scaffold(
        bottomBar = { MainBottomAppBar(navController, 2) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            ARSceneView(
                modifier = Modifier.fillMaxSize(),
                planeRenderer = false,
                // 🔥 This is the exact callback parameter in SceneView 4.X
                onSessionCreated = { session ->
                    val config = session.config
                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE

                    // Build and inject your augmented image database
                    config.augmentedImageDatabase = viewModel.buildAugmentedImageDatabase(session)

                    // Apply the configuration adjustments to the session instance
                    session.configure(config)
                },
                onSessionUpdated = { _, frame ->
                    val updatedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
                    for (augImage in updatedImages) {
                        if (augImage.trackingState == TrackingState.TRACKING &&
                            augImage.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
                        ) {
                            viewModel.onImageRecognized(augImage)
                        }
                    }
                }
            )

            // 3. UI Overlay
            recognized?.let { data ->
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text("Riconosciuta immagine:", color = Color.White)
                    Text("Folder: ${data.folder}", color = Color.White)
                    Text("Model: ${data.model}", color = Color.White)
                    Text("Type: ${data.type}", color = Color.White)
                }
            }
        }
    }

    // 2. Pure Jetpack Compose Implementation (SceneView 4.18.0)

}