package com.example.smartpagesar.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.PointF
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Position
import io.github.sceneview.rememberModelInstance
import com.example.smartpagesar.R
import com.google.android.filament.Box
import io.github.sceneview.math.Scale
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.rememberARView
import io.github.sceneview.rememberModelLoader
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled._360
import androidx.compose.material.icons.filled.BorderBottom
import androidx.compose.material.icons.filled.BorderLeft
import androidx.compose.material.icons.filled.BorderStyle
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import io.github.sceneview.managers.getParentOrNull
import java.io.File
import java.nio.ByteBuffer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.example.smartpagesar.data.models.InteractiveModelType
import com.example.smartpagesar.data.models.NodeExtras
import com.example.smartpagesar.ui.composables.CustomDescription
import com.example.smartpagesar.ui.viewmodels.AnimationSpeed
import com.example.smartpagesar.ui.viewmodels.SettingsState
import com.google.ar.core.CameraConfigFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.EnumSet
import kotlin.collections.emptyList
import kotlin.math.abs


@SuppressLint("AutoboxingStateCreation")
@Composable
fun ARScreen(
    navController: NavController,
    viewModel: ARScreenViewModel,
    settingsState: SettingsState
) {
    val context = LocalContext.current
    val recognized by viewModel.recognizedImage.collectAsState()

    var rotation by remember { mutableFloatStateOf(0.0f) }
    var scale by remember { mutableFloatStateOf(0.04f) }
    var autoRotate by remember { mutableStateOf(false) }

    var anchor by remember { mutableStateOf<Anchor?>(null) }

    var isScanning by remember { mutableStateOf(true) }
    var loadedModelInstance by remember { mutableStateOf<ModelInstance?>(null) }

    var nodeSelected by remember { mutableStateOf<Node?>(null) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    var labelScreenPosition by remember { mutableStateOf<PointF?>(null) }
    var labelOffset by remember { mutableStateOf(Offset.Unspecified) }
    var selectedNodeExtras by remember { mutableStateOf<NodeExtras?>(null) }
    var activeNodesList by remember { mutableStateOf<List<Node>>(emptyList()) }


    var animationSpeed by remember { mutableStateOf(AnimationSpeed.SPEED_1X) }
    var isAnimationPlaying by remember { mutableStateOf(false) }
    var isAnimationLooping by remember { mutableStateOf(false) }
    var animationElapsedTime by remember { mutableFloatStateOf(0f) }

    var selectedSection by remember { mutableIntStateOf(0) }

    var currentBuildStep by remember { mutableIntStateOf(0) }
    var currentStepNode by remember { mutableStateOf<Node?>(null) }
    var maxStepNumber by remember { mutableIntStateOf(0) }

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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

// 2. Automatically request permission when the Composable enters the screen
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.permission_camera),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // 3. Give them a button to manually trigger it if they dismiss it
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }
        return
    }

    LaunchedEffect(recognized) {
        val currentRecognized = recognized
        if (currentRecognized != null) {
            val localFile = File(context.filesDir, "${currentRecognized.folder}/${currentRecognized.model}.glb")

            if (localFile.exists()) {
                // Move file loading and buffering off the Main UI thread completely
                val buffer = withContext(Dispatchers.IO) {
                    val fileBytes = localFile.readBytes()
                    ByteBuffer.wrap(fileBytes)
                }
                // Pass the ready buffer back to the main thread's renderer
                loadedModelInstance = modelLoader.createModelInstance(buffer)
            }
        } else {
            loadedModelInstance = null
        }
    }

    // Put this at the top level of your ARScreen Composable function
    LaunchedEffect(selectedSection, currentBuildStep, activeNodesList) {
        if (activeNodesList.isEmpty()) return@LaunchedEffect

        when (recognized?.type?.toInt()) {
            3 -> { // Type 3: Section View visibility checks
                when (selectedSection) {
                    0 -> activeNodesList.forEach { it.isVisible = true }
                    1 -> activeNodesList.forEach { it.isVisible = it.name != "top-left" }
                    2 -> activeNodesList.forEach { it.isVisible = it.name != "top-left" && it.name != "bottom-left" }
                    3 -> activeNodesList.forEach { it.isVisible = it.name != "top-left" && it.name != "top-right" }
                }
            }
            4 -> { // Type 4: Construction step calculations
                maxStepNumber = activeNodesList.size

                // Updates your UI description card text layout instantly
                currentStepNode = activeNodesList.find { it.name == "step-$currentBuildStep" }

                activeNodesList.forEach { node ->
                    val stepIndex = node.name?.substringAfter("step-")?.toIntOrNull()
                    if (stepIndex != null) {
                        node.isVisible = stepIndex <= currentBuildStep
                    }
                }
            }
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
                planeRenderer = false,
                onGestureListener = null,
                sessionConfiguration = { session, config ->
                    config.lightEstimationMode = settingsState.lighting
                    config.focusMode = Config.FocusMode.FIXED
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        config.depthMode = Config.DepthMode.DISABLED
                    }
                    config.updateMode = Config.UpdateMode.BLOCKING
                    config.augmentedImageDatabase = viewModel.buildAugmentedImageDatabase(session)
                },
                onSessionCreated = { session ->
                    try {
                        val filter = CameraConfigFilter(session).apply {
                            targetFps = EnumSet.of(settingsState.fps)
                        }
                        val configs = session.getSupportedCameraConfigs(filter)
                        if (configs.isNotEmpty()) {
                            val optimizedConfig = configs.minByOrNull { it.imageSize.width * it.imageSize.height }
                            if (optimizedConfig != null) {
                                session.cameraConfig = optimizedConfig
                            }
                        }
                    } catch (e: Exception) {
                        println(e)
                    }
                },
                onSessionUpdated = { _, frame ->
                    if (autoRotate){
                        rotation = (rotation + 0.5f) % 360f
                    }
                    if (isScanning) {
                        val updatedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
                        for (augImage in updatedImages) {
                            if (augImage.trackingState == TrackingState.TRACKING &&
                                augImage.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
                            ) {
                                viewModel.onImageRecognized(augImage)

                                anchor = augImage.createAnchor(augImage.centerPose)


                                isScanning = false
                                break
                            }
                        }
                    }
                    val selected = nodeSelected
                    if (selected != null) {
                        val worldPos = selected.worldPosition
                        val rawViewPos = cameraNode.worldToView(worldPos)

                        val viewport = view.viewport
                        val viewWidth = viewport.width.toFloat()
                        val viewHeight = viewport.height.toFloat()

                        if (abs(rawViewPos.x) <= 2.0f && abs(rawViewPos.y) <= 2.0f) {
                            // Creates an unboxed primitive value — zero garbage collection penalty!
                            labelOffset = Offset(
                                x = rawViewPos.x * viewWidth,
                                y = (1.0f - rawViewPos.y) * viewHeight
                            )
                        } else {
                            labelOffset = Offset.Unspecified
                        }
                    } else {
                        labelOffset = Offset.Unspecified
                    }

                    if (isAnimationPlaying && loadedModelInstance != null) {
                        val animator = loadedModelInstance!!.animator
                        if (animator.animationCount > 0) {
                            val deltaSec = (1f / 60f) * animationSpeed.speed
                            animationElapsedTime += deltaSec

                            for (i in 0 until animator.animationCount) {
                                val duration = animator.getAnimationDuration(i)

                                var timeToApply = animationElapsedTime
                                if (!isAnimationLooping && animationElapsedTime > duration) {
                                    timeToApply = duration // Freeze at final frame if looping is off
                                } else if (isAnimationLooping) {
                                    timeToApply = animationElapsedTime % duration // Wrap back around
                                }

                                animator.applyAnimation(i, timeToApply)
                            }
                            animator.updateBoneMatrices()
                        }
                    }
                }
            ) {
                val currentAnchor = anchor
                val currentRecognized = recognized

                if (currentAnchor != null && currentRecognized != null) {
                    AnchorNode(
                        anchor = currentAnchor,
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


                            key(loadedModelInstance) {
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

                                            activeNodesList = this.nodes

                                            if (recognized?.let { image -> image.type.toInt() == 2 }?: false){

                                                this.onSingleTapConfirmed = { e ->
                                                    view.pick(e.x.toInt(), view.viewport.height - e.y.toInt(), mainHandler) { result ->
                                                        val hitEntity = result.renderable
                                                        if (hitEntity != 0) {
                                                            var current = hitEntity
                                                            while (current != 0 && model.getName(current).isNullOrEmpty()) {
                                                                current = engine.transformManager.getParentOrNull(current) ?: 0
                                                            }
                                                            val tappedNode = nodes.find { it.entity == current } ?: nodes.find { it.entity == hitEntity }

                                                            val rm = engine.renderableManager

                                                            nodeSelected?.let { prev ->
                                                                val prevInstance = rm.getInstance(prev.entity)
                                                                if (prevInstance != 0) {
                                                                    val prevMat = rm.getMaterialInstanceAt(prevInstance, 0)

                                                                    prevMat.setParameter(
                                                                        "emissiveFactor",0.0f, 0.0f, 0.0f
                                                                    )
                                                                }
                                                            }

                                                            // --- HIGHLIGHT NEW SELECTION ---
                                                            val instance = rm.getInstance(tappedNode?.entity ?: 0)
                                                            if (instance != 0) {
                                                                val mat = rm.getMaterialInstanceAt(instance, 0)

                                                                mat.setParameter(
                                                                    "emissiveFactor",1.0f, 0.5f, 0.0f
                                                                )
                                                            }

                                                            nodeSelected = tappedNode
                                                            selectedNodeExtras = parseNodeExtras((tappedNode as? ModelNode.ChildNode)?.extras)
                                                        }
                                                    }
                                                    true
                                                }
                                            }
                                            else{
                                                this.onFrame = null
                                                this.onSingleTapConfirmed = null
                                            }
                                        }
                                    )
                                }
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
                        loadedModelInstance = null
                        isScanning = true
                        scale = 0.04f
                        rotation = 0.0f
                        autoRotate = false
                        labelScreenPosition = null

                        isAnimationPlaying = false
                        isAnimationLooping = false

                        selectedSection = 0

                        currentBuildStep = 0
                        currentStepNode = null
                        maxStepNumber = 0
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 14.dp, top = 10.dp),
                containerColor = if (isScanning) Color.Red else MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = if (isScanning) stringResource(R.string.ar_scanning) else stringResource(R.string.ar_rescan),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.White
                )
            }


            recognized?.let { data ->
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 10.dp, start = 14.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(stringResource(R.string.recognized_image), color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Model: ${data.model}", color = Color.White)
                        Icon(InteractiveModelType.entries[data.type.toInt()].icon, "icon", tint = Color.White)
                    }

                }
                when (data.type.toInt()){
                    0 -> {
                        Column(modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            )
                        ) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                            ) {

                                FilledIconToggleButton(
                                    modifier = Modifier.padding(10.dp),
                                    checked = autoRotate,
                                    onCheckedChange = { autoRotate = !autoRotate }
                                ) { Icon(Icons.Filled.LockReset, "lockRotate") }

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
                                .padding(10.dp, 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ZoomOutMap, "scale", tint = MaterialTheme.colorScheme.onSecondary)
                                Slider(
                                    modifier = Modifier.padding(start = 5.dp),
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
                    }
                    1 -> {
                        Column(modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .shadow(4.dp, RoundedCornerShape(15.dp))
                                        .size(60.dp),
                                    onClick = {
                                        animationSpeed = when (animationSpeed) {
                                            AnimationSpeed.SPEED_1X -> {
                                                AnimationSpeed.SPEED_05X
                                            }
                                            AnimationSpeed.SPEED_05X -> {
                                                AnimationSpeed.SPEED_025X
                                            }
                                            AnimationSpeed.SPEED_025X -> {
                                                AnimationSpeed.SPEED_2X
                                            }
                                            AnimationSpeed.SPEED_2X -> {
                                                AnimationSpeed.SPEED_1X
                                            }
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Text("${animationSpeed.speed}x", fontSize = 20.sp)
                                }
                                IconButton(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .shadow(4.dp, RoundedCornerShape(15.dp))
                                        .size(80.dp),
                                    onClick = { isAnimationPlaying = !isAnimationPlaying },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    if(isAnimationPlaying){
                                        Icon(Icons.Filled.Pause, "pause")
                                    }else{
                                        Icon(Icons.Filled.PlayArrow, "play", modifier = Modifier.size(40.dp))
                                    }
                                }
                                IconToggleButton(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .shadow(4.dp, RoundedCornerShape(15.dp))
                                        .size(60.dp),
                                    checked = isAnimationLooping,
                                    onCheckedChange = { value -> isAnimationLooping = value },
                                    colors = IconButtonDefaults.iconToggleButtonColors(
                                        containerColor = MaterialTheme.colorScheme.outline,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedContainerColor = MaterialTheme.colorScheme.primary,
                                        checkedContentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                ){
                                    Icon(Icons.Filled.Loop, "loop", modifier = Modifier.size(40.dp))
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ZoomOutMap, "scale", tint = MaterialTheme.colorScheme.onSecondary)
                                Slider(
                                    modifier = Modifier.padding(start = 5.dp),
                                    value = scale,
                                    onValueChange = { value ->
                                        scale = value
                                        // Update root or selected node imperatively
                                        nodeSelected?.let { it.scale = Scale(value) }
                                    },
                                    valueRange = 0.01f..0.1f,
                                )
                            }
                        }
                    }
                    2 -> {
                        Column(modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            )
                        ) {
                            CustomDescription(stringResource(R.string.tooltip_parts), Color.White)
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp, 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled._360, "rotate", tint = MaterialTheme.colorScheme.onSecondary)
                                Slider(
                                    modifier = Modifier.padding(5.dp),
                                    enabled = !autoRotate,
                                    value = rotation,
                                    onValueChange = { value ->
                                        rotation = value
                                    },
                                    valueRange = 0f..360f,
                                )
                            }
                        }
                        if (labelOffset != Offset.Unspecified){
                            val nodeInfo = selectedNodeExtras

                            Box(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .absoluteOffset { IntOffset(labelOffset.x.toInt(), labelOffset.y.toInt()) }
                                        .wrapContentSize(Alignment.Center)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .background(
                                                Color.Black.copy(alpha = 0.8f),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        val description = nodeInfo?.description ?: "Selected Component"
                                        val labelTitle = (nodeSelected as? ModelNode.ChildNode)?.name ?: "label"
                                        Text(labelTitle, fontSize = 20.sp, color = Color.White)
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 3.dp))
                                        Text(
                                            text = description,
                                            fontSize = 16.sp,
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }

                    }
                    3 -> {
                        Column(modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                IconButton(
                                    enabled = selectedSection == 0 || selectedSection == 1,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .shadow(
                                            if (selectedSection == 0 || selectedSection == 1) 4.dp else 0.dp,
                                            RoundedCornerShape(15.dp)
                                        )
                                        .size(80.dp),
                                    onClick = {
                                        if (selectedSection == 0){
                                            selectedSection = 1
                                        }else if (selectedSection == 1){
                                            selectedSection = 0
                                        }
                                              },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Icon(Icons.Default.BorderStyle, "")
                                }
                                IconButton(
                                    enabled = selectedSection == 0 || selectedSection == 2,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .shadow(
                                            if (selectedSection == 0 || selectedSection == 2) 4.dp else 0.dp,
                                            RoundedCornerShape(15.dp)
                                        )
                                        .size(80.dp),
                                    onClick = {
                                        if (selectedSection == 0){
                                            selectedSection = 2
                                        }else if (selectedSection == 2){
                                            selectedSection = 0
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Icon(Icons.Default.BorderLeft, "")
                                }
                                IconButton(
                                    enabled = selectedSection == 0 || selectedSection == 3,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .shadow(
                                            if (selectedSection == 0 || selectedSection == 3) 4.dp else 0.dp,
                                            RoundedCornerShape(15.dp)
                                        )
                                        .size(80.dp),
                                    onClick = {
                                        if (selectedSection == 0){
                                            selectedSection = 3
                                        }else if (selectedSection == 3){
                                            selectedSection = 0
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Icon(Icons.Default.BorderBottom, "")
                                }
                            }
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp, 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled._360, "rotate", tint = MaterialTheme.colorScheme.onSecondary)
                                Slider(
                                    modifier = Modifier.padding(5.dp),
                                    enabled = !autoRotate,
                                    value = rotation,
                                    onValueChange = { value ->
                                        rotation = value
                                    },
                                    valueRange = 0f..360f,
                                )
                            }
                        }
                    }
                    4 -> {
                        Column(modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            )
                        ) {
                            currentStepNode?.let {
                                val nodeInfo = parseNodeExtras((currentStepNode as? ModelNode.ChildNode)?.extras)

                                CustomDescription(nodeInfo.description!!, Color.White)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .shadow(4.dp, RoundedCornerShape(15.dp))
                                        .size(80.dp),
                                    onClick = { currentBuildStep = 0 },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Icon(Icons.Default.Restore, "reset")
                                }
                                IconButton(
                                    enabled = currentBuildStep > 0,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .shadow(
                                            if (currentBuildStep > 0) 4.dp else 0.dp,
                                            RoundedCornerShape(15.dp)
                                        )
                                        .size(80.dp),
                                    onClick = { if (currentBuildStep > 0) currentBuildStep-- },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, "back")
                                }
                                IconButton(
                                    enabled = currentBuildStep < maxStepNumber-1,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .shadow(
                                            if (currentBuildStep < maxStepNumber - 1) 4.dp else 0.dp,
                                            RoundedCornerShape(15.dp)
                                        )
                                        .size(80.dp),
                                    onClick = { currentBuildStep++ },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, "forward")
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ZoomOutMap, "scale", tint = MaterialTheme.colorScheme.onSecondary)
                                Slider(
                                    modifier = Modifier.padding(start = 5.dp),
                                    value = scale,
                                    onValueChange = { value ->
                                        scale = value
                                        nodeSelected?.let { it.scale = Scale(value) }
                                    },
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

val gltfJsonDecoder = Json {
    ignoreUnknownKeys = true // Prevents crashes if Blender exports extra metadata you don't care about
    coerceInputValues = true // Falls back to your data class defaults if something is null in JSON
}

fun parseNodeExtras(rawExtras: String?): NodeExtras {
    if (rawExtras.isNullOrEmpty()) return NodeExtras("")

    return runCatching {
        gltfJsonDecoder.decodeFromString<NodeExtras>(rawExtras)
    }.getOrElse { NodeExtras("") }// Returns null safely if the string wasn't valid JSON
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