package com.example.smartpagesar.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RotateLeft
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
import androidx.xr.arcore.hitTest
import com.example.smartpagesar.ui.composables.MainBottomAppBar
import com.example.smartpagesar.ui.viewmodels.ARScreenViewModel
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Position
import io.github.sceneview.rememberModelInstance
import com.example.smartpagesar.R
import com.google.android.filament.Box
import io.github.sceneview.ar.ARScene
import io.github.sceneview.math.Scale
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.rememberARView
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.model.model
import android.os.Handler
import android.os.Looper
import androidx.core.graphics.blue
import io.github.sceneview.managers.getParentOrNull
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

    var nodeSelected by remember { mutableStateOf<Node?>(null) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val view = rememberARView(engine)
    val cameraNode = rememberARCameraNode(engine)


    // Standard camera permission flow...
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

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

                val fileBytes = localFile.readBytes()
                val buffer = ByteBuffer.wrap(fileBytes)


                loadedModelInstance = modelLoader.createModelInstance(buffer)
            }
        } else {

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
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                cameraNode = cameraNode,
                planeRenderer = true,
                onGestureListener = null,
                sessionConfiguration = { session, config ->
                    config.focusMode = Config.FocusMode.FIXED
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        config.depthMode = Config.DepthMode.AUTOMATIC
                    }
                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    config.augmentedImageDatabase = viewModel.buildAugmentedImageDatabase(session)
                },
                onSessionCreated = { session ->
                    try {
                        val filter = com.google.ar.core.CameraConfigFilter(session).apply {
                            targetFps = java.util.EnumSet.of(com.google.ar.core.CameraConfig.TargetFps.TARGET_FPS_30)
                        }
                        val configs = session.getSupportedCameraConfigs(filter)
                        if (configs.isNotEmpty()) {
                            val optimizedConfig = configs.minByOrNull { it.imageSize.width * it.imageSize.height }
                            if (optimizedConfig != null) {
                                session.cameraConfig = optimizedConfig
                            }
                        }
                    } catch (e: Exception) {
                    }
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

                                anchor = session.createAnchor(augImage.centerPose)


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
                    AnchorNode(
                        anchor = currentAnchor,
                        updateAnchorPose = false,
                    ) {

                        rememberModelInstance(modelLoader, "models/platform.glb")?.let { platformInstance ->


                            val platformHeight = remember(platformInstance) {
                                getTrueHeight(platformInstance.asset.boundingBox, 0.10f)
                            }


                            ModelNode(
                                modelInstance = platformInstance,
                                scaleToUnits = 0.10f,
                                centerOrigin = Position(0f, 0f, 0f)
                            )



                            if (loadedModelInstance != null) {
                                val boundingBoxHeight = loadedModelInstance?.asset?.boundingBox?.halfExtent[1]

                                ModelNode(
                                    modelInstance = loadedModelInstance!!,
                                    scaleToUnits = null,
                                    scale = Scale(scale),
                                    rotation = Rotation(0.0f, rotation, 0.0f),
                                    autoAnimate = false,
                                    position = Position(
                                        0.0f,
                                        platformHeight + boundingBoxHeight!! * scale,
                                        0.0f
                                    ),
                                    apply = {

                                        // Auto-select root on load if nothing selected
                                        if (nodeSelected == null) nodeSelected = this

                                        this.onSingleTapConfirmed = { e ->
                                            view.pick(e.x.toInt(), view.viewport.height - e.y.toInt(), mainHandler) { result ->
                                                val hitEntity = result.renderable
                                                if (hitEntity != 0) {
                                                    var current = hitEntity
                                                    // Find the first parent with a name in the glTF hierarchy
                                                    while (current != 0 && model.getName(current).isNullOrEmpty()) {
                                                        current = engine.transformManager.getParentOrNull(current) ?: 0
                                                    }
                                                    val tappedNode = nodes.find { it.entity == current } ?: nodes.find { it.entity == hitEntity }

                                                    val rm = engine.renderableManager

                                                    nodeSelected?.let { prev ->
                                                        val prevInstance = rm.getInstance(prev.entity)
                                                        if (prevInstance != 0) {
                                                            val prevMat = rm.getMaterialInstanceAt(prevInstance, 0)
                                                            prevMat.setParameter("emissiveFactor", 0f, 0f, 0f)
                                                        }
                                                    }

                                                    val instance = rm.getInstance(tappedNode?.entity
                                                        ?: 0)
                                                    if (instance != 0) {
                                                        val mat = rm.getMaterialInstanceAt(instance, 0)
                                                        mat.setParameter("emissiveFactor", 1f, 0.5f, 0f) // orange glow
                                                    }

                                                    nodeSelected = tappedNode
                                                }
                                            }
                                            true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }


            FloatingActionButton(
                onClick = {
                    if (!isScanning) {
                        viewModel.resetImage()
                        nodeSelected = null
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
                    nodeSelected?.let {
                        val nodeName = (it as? ModelNode.ChildNode)?.extras ?: "Root"
                        Text("Selected: $nodeName", color = Color.Yellow)
                    }
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
                                    onValueChange = { value ->
                                        rotation = value
                                        // Update root or selected node imperatively
                                        nodeSelected?.let { it.rotation = Rotation(0f, value, 0f) }
                                    },
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
                                    onValueChange = { value ->
                                        scale = value
                                        // Update root or selected node imperatively
                                        nodeSelected?.let { it.scale = Scale(value) }
                                    },
                                    valueRange = 0.01f..0.06f,
                                )
                            }
                        }
                        2 -> {}
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

    val rawMaxDimension = maxOf(px, maxOf(py, pz))
    val rawHalfHeight = boundingBox.halfExtent[1]
    val scaleFactor = scaleFactor / (rawMaxDimension * 2f)
    return (rawHalfHeight * 2f) * scaleFactor
}