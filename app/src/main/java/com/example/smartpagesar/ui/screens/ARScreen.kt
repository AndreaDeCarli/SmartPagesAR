package com.example.smartpagesar.ui.screens

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
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
import com.example.smartpagesar.R
import com.google.android.filament.Box
import io.github.sceneview.geometries.BoundingBox
import io.github.sceneview.math.Scale
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import java.io.File
import java.nio.ByteBuffer


@Composable
fun ARScreen(
    navController: NavController,
    viewModel: ARScreenViewModel
) {
    val context = LocalContext.current
    val recognized by viewModel.recognizedImage.collectAsState()

    var rotation by remember { mutableFloatStateOf(0.0f) }
    var scale by remember { mutableFloatStateOf(0.04f) }
    var autoRotate by remember { mutableStateOf(false) }

    var anchor by remember { mutableStateOf<Anchor?>(null) }
    var hasAnchored by remember { mutableStateOf(false) }

    // Track whether we are actively scanning (True) or locked (False)
    var isScanning by remember { mutableStateOf(true) }
    var loadedModelInstance by remember { mutableStateOf<ModelInstance?>(null) }


    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)


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

    LaunchedEffect(recognized) {
        val currentRecognized = recognized
        if (currentRecognized != null) {
            val localFile = File(context.filesDir, "${currentRecognized.folder}/${currentRecognized.model}.glb")
            if (localFile.exists()) {
                // Perform heavy disk reading asynchronously on a background thread pool
                val fileBytes = localFile.readBytes()
                val buffer = ByteBuffer.wrap(fileBytes)

                // Instantiate the structural 3D data once and cache it in our state hook
                loadedModelInstance = modelLoader.createModelInstance(buffer)
            }
        } else {
            // Clear out cache memory when tracking is reset
            loadedModelInstance = null
        }
    }

    Scaffold(
        bottomBar = { MainBottomAppBar(navController, 2) }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            ARSceneView(
                engine = engine,
                modelLoader = modelLoader,
                modifier = Modifier.fillMaxSize(),
                planeRenderer = false,
                onSessionCreated = { session ->
                    val config = session.config
                    config.focusMode = Config.FocusMode.FIXED
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)){
                        config.depthMode = Config.DepthMode.AUTOMATIC
                    }
                    try {
                        // Filter for a stable frame rate, but avoid overwhelming resolutions
                        val filter = com.google.ar.core.CameraConfigFilter(session).apply {
                            targetFps = java.util.EnumSet.of(com.google.ar.core.CameraConfig.TargetFps.TARGET_FPS_30)
                        }
                        val configs = session.getSupportedCameraConfigs(filter)
                        if (configs.isNotEmpty()) {
                            // OPTIMIZATION: Pick a mid-to-low resolution instead of the absolute maximum!
                            val optimizedConfig = configs.minByOrNull { it.imageSize.width * it.imageSize.height }
                            if (optimizedConfig != null) {
                                session.cameraConfig = optimizedConfig
                            }
                        }
                    } catch (e: Exception) {
                        // Fallback
                    }
                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    config.augmentedImageDatabase = viewModel.buildAugmentedImageDatabase(session)
                    session.configure(config)
                },
                onSessionUpdated = { session, frame ->
                    if (autoRotate){
                        rotation += 0.5f
                        if (rotation >= 360f){ rotation = 0.0f }
                    }
                    if (isScanning && !hasAnchored) {
                        val updatedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
                        for (augImage in updatedImages) {
                            if (augImage.trackingState == TrackingState.TRACKING &&
                                augImage.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
                            ) {
                                viewModel.onImageRecognized(augImage)

                                // 2. CRITICAL CHANGE: Create a generic Session World Anchor from the pose coordinates
                                // instead of creating an image-linked trackable anchor (augImage.createAnchor).
                                // This creates a rigid point locked to the room's global physics matrix.
                                anchor = session.createAnchor(augImage.centerPose)

                                // Freeze updates instantly
                                hasAnchored = true
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
                    AnchorNode(anchor = currentAnchor, updateAnchorPose = false) {

                        rememberModelInstance(modelLoader, "models/platform.glb")?.let { platformInstance ->

                            // Cache the platform's layout parameters cleanly
                            val platformHeight = remember(platformInstance) {
                                getTrueHeight(platformInstance.asset.boundingBox, 0.10f)
                            }

                            // 1. Render the Base Platform Node
                            ModelNode(
                                modelInstance = platformInstance,
                                scaleToUnits = 0.10f,
                                centerOrigin = Position(0f, 0f, 0f)
                            )

                            // 2. Load the dynamic sub-model asset

                            if (loadedModelInstance != null) {
                                val boundingBoxHeight = loadedModelInstance?.asset?.boundingBox?.halfExtent[1]

                                // 4. Correctly nest the character node directly within the platform's hierarchy slot
                                ModelNode(
                                    modelInstance = loadedModelInstance!!,
                                    scaleToUnits = null,
                                    scale = Scale(scale),
                                    rotation = Rotation(0.0f, rotation, 0.0f),
                                    autoAnimate = false,
                                    // Keeps feet planted firmly on top of the platform regardless of current size scaling
                                    position = Position(
                                        0.0f,
                                        platformHeight + boundingBoxHeight!! * scale,
                                        0.0f
                                    )
                                )
                            }
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
                        hasAnchored = false
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
                    Text(stringResource(R.string.recognized_image), color = Color.White)
                    Text("Model: ${data.model}", color = Color.White)
                }
                Column(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth() ) {
                    when (data.type.toInt()){
                        0 -> {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                            ) {

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
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                            ) {
                                Slider(
                                    modifier = Modifier.padding(10.dp),
                                    value = scale,
                                    onValueChange = {value -> scale = value},
                                    valueRange = 0.01f..0.06f,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getTrueHeight(boundingBox: Box, scaleFactor: Float): Float{
    val px = boundingBox.halfExtent[0]
    val py = boundingBox.halfExtent[1]
    val pz = boundingBox.halfExtent[2]
    // Find the largest raw dimension
    val rawMaxDimension = maxOf(px, maxOf(py, pz))
    val rawHalfHeight = boundingBox.halfExtent[1]
    val scaleFactor = scaleFactor / (rawMaxDimension * 2f)
    return (rawHalfHeight * 2f) * scaleFactor
}