package com.example.smartpagesar.ui.viewmodels

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.ar.core.AugmentedImage
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class ARScreenViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    // Event when an image is recognized
    private val _recognizedImage = MutableStateFlow<RecognizedImageData?>(null)
    val recognizedImage = _recognizedImage.asStateFlow()

    // Helper function to build and provide the database to ARSceneView
    fun buildAugmentedImageDatabase(session: Session): AugmentedImageDatabase {
        val context = getApplication<Application>()
        val db = AugmentedImageDatabase(session)

        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) return db

        imagesDir.listFiles()?.forEach { file ->
            if (file.extension.lowercase() in listOf("jpg", "jpeg", "png")) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    val name = file.nameWithoutExtension
                    db.addImage(name, bitmap, 0.09f)
                }
            }
        }
        return db
    }

    fun onImageRecognized(image: AugmentedImage) {
        val name = image.name ?: return

        val parts = name.split("-")
        if (parts.size < 3) return

        _recognizedImage.value = RecognizedImageData(
            folder = parts[0],
            model = parts[1],
            type = parts[2],
            trackingMethod = image.trackingMethod
        )
    }
    fun resetImage(){
        _recognizedImage.value = null
    }
}

data class RecognizedImageData(
    val folder: String,
    val model: String,
    val type: String,
    val trackingMethod: AugmentedImage.TrackingMethod
)

class ARScreenViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ARScreenViewModel(app) as T
    }
}

enum class Speeds(val speed: Float){
    SPEED_1X(1.0f),
    SPEED_025X(0.25f),
    SPEED_05X(0.5f),
    SPEED_2X(2.0f),
}
