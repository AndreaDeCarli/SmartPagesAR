package com.example.smartpagesar.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ARSceneView // Ensure correct import for 4.X Composable
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberModelInstance
import java.io.File
import java.nio.ByteBuffer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARScreen(
    navController: NavController,
    viewModel: ARScreenViewModel
) {
    val context = LocalContext.current
    val recognized by viewModel.recognizedImage.collectAsState()

    var rotation by remember { mutableFloatStateOf(0.0f) }
    var autoRotate by remember { mutableStateOf(false) }

    var anchor by remember { mutableStateOf<Anchor?>(null) }

    // Track whether we are actively scanning (True) or locked (False)
    var isScanning by remember { mutableStateOf(true) }

    // Standard camera permission flow...
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    // (Include your rememberLauncherForActivityResult and LaunchedEffect here)

    if (!hasCameraPermission) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Consenti la fotocamera per usare la realtà aumentata")
        }
        return
    }

    Scaffold(
        bottomBar = { MainBottomAppBar(navController, 2) }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            ARSceneView(
                modifier = Modifier.fillMaxSize(),
                planeRenderer = false,
                onSessionCreated = { session ->
                    val config = session.config
                    config.focusMode = Config.FocusMode.AUTO

                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    config.augmentedImageDatabase = viewModel.buildAugmentedImageDatabase(session)
                    session.configure(config)
                },
                onSessionUpdated = { session, frame ->
                    // 1. ONLY process tracking if the user is actively scanning
                    if (autoRotate){
                        rotation += 0.5f
                        if (rotation >= 360f){
                            rotation = 0.0f
                        }
                    }
                    if (isScanning) {
                        val updatedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
                        for (augImage in updatedImages) {
                            if (augImage.trackingState == TrackingState.TRACKING &&
                                augImage.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
                            ) {
                                viewModel.onImageRecognized(augImage)

                                // Create the world anchor to lock it to the room physics
                                anchor = session.createAnchor(augImage.centerPose)

                                // 2. Flip the state to false immediately!
                                // This turns off scanning and locks everything down.
                                isScanning = false
                                break
                            }
                        }
                    }
                }
            ) {
                val currentAnchor = anchor
                val currentRecognized = recognized

                if (currentAnchor != null && currentRecognized != null) {
                    AnchorNode(anchor = currentAnchor) {
                        val localFile = File(context.filesDir, "${currentRecognized.folder}/${currentRecognized.model}.glb")
                        if (localFile.exists()) {
                            val fileBytes = remember(localFile) { localFile.readBytes() }
                            val modelInstance = remember(fileBytes) {
                                val buffer = ByteBuffer.wrap(fileBytes)
                                modelLoader.createModelInstance(buffer)
                            }
                            ModelNode(
                                modelInstance = modelInstance,
                                scaleToUnits = 0.2f,
                                rotation = Rotation(0.0f,rotation,0.0f),
                                centerOrigin = Position(0f, 0f, 0f)
                            )
                        }
                    }
                }
            }

            // 3. Floating Action Scan Control Button
            FloatingActionButton(
                onClick = {
                    if (!isScanning) {
                        viewModel.resetImage()
                        // Reset button: Clear old anchors and flip scanning back on
                        anchor = null
                        isScanning = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 14.dp, top = 10.dp),
                containerColor = if (isScanning) Color.Red else MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = if (isScanning) "Scanning..." else "Click to Scan Again",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.White
                )
            }

            // UI Text Overlay Display
            recognized?.let { data ->
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 10.dp, start = 14.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text("Riconosciuta immagine:", color = Color.White)
                    Text("Model: ${data.model}", color = Color.White)
                }
                Column(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth() ) {
                    when (data.type.toInt()){
                        0 -> {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)) {

                                FilledIconToggleButton(
                                    modifier = Modifier.padding(10.dp),
                                    checked = autoRotate,
                                    onCheckedChange = { autoRotate = !autoRotate }
                                ) { Icon(Icons.Filled.RotateLeft, "rotate") }

                                Slider(
                                    modifier = Modifier.padding(10.dp),
                                    enabled = !autoRotate,
                                    value = rotation,
                                    onValueChange = {value -> rotation = value},
                                    valueRange = 0f..360f,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}