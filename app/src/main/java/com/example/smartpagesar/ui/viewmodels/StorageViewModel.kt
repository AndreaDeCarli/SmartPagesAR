package com.example.smartpagesar.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Model(
    val name: String,
    val fileSize: Float
)

data class BookFolder(
    val name: String,
    val models: List<Model>
)


class StorageViewModel(
    val context: Context
): ViewModel() {

    private val _totalSize = MutableStateFlow(0L)
    val totalSize = _totalSize.asStateFlow()

    private val _folders = MutableStateFlow<List<BookFolder>>(emptyList())
    val folders = _folders.asStateFlow()

    init {
        loadFolders()
    }

    fun loadFolders(){
        val filesDir = context.filesDir
        filesDir.listFiles()?.forEach { file ->
            if (file.isDirectory && file.name != "datastore") {
                val models = file.listFiles()?.map { modelFile ->
                    _totalSize.value+= modelFile.length()
                    Model(
                        name = modelFile.name,
                        fileSize = modelFile.length()/(1024 * 1024).toFloat()
                    )
                } ?: emptyList()
                _folders.value += BookFolder(
                    name = file.name,
                    models = models
                )
            }
        }
    }

}

class StorageViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StorageViewModel(context) as T
    }
}