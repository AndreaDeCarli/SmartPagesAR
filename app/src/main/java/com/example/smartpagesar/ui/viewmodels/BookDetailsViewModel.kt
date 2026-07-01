package com.example.smartpagesar.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.InteractiveModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class BookDetailViewModel(
    private val supabase: SupabaseClient,
    val bookId: String,
    private val context: Context
) : ViewModel() {

    private val _models = MutableStateFlow<List<InteractiveModel>>(emptyList())
    private val _book = MutableStateFlow(Book("",0,"","",null,0,0))
    val book = _book.asStateFlow()
    val models = _models.asStateFlow()

    init {
        loadBook()
        loadModels()
    }

    private fun loadBook(){
        viewModelScope.launch {
            val result = supabase.postgrest["Books"]
                .select {
                    filter {
                        eq("id", bookId)
                    }
                }
                .decodeSingle<Book>()
            _book.value = result
        }
    }
    private fun loadModels() {
        viewModelScope.launch {
            val result = supabase.postgrest["interactive_model"]
                .select { filter { eq("book_id", bookId) } }
                .decodeList<InteractiveModel>()

            _models.value = result
        }
    }

    fun downloadSingleModel(model: InteractiveModel,shortId: Int){
        viewModelScope.launch {
            val modelBytes = supabase.storage
                .from("ModelsImages")
                .downloadPublic("book${shortId}-${model.id}-${model.type}.glb")

            saveToInternalStorage(
                fileName = "${model.id}.glb",
                bytes = modelBytes,
                folderName = "book${shortId}"
            )

            val imageBytes = supabase.storage
                .from("ModelsImages")
                .downloadPublic("book${shortId}-${model.id}-${model.type}.png")

            saveToInternalStorage(
                fileName = "book${shortId}-${model.id}-${model.type}.png",
                bytes = imageBytes,
                folderName = "images"
            )
        }

    }

    private fun saveToInternalStorage(
        folderName: String,
        fileName: String,
        bytes: ByteArray
    ): String {

        val dir = File(context.filesDir, folderName)
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, fileName)
        file.writeBytes(bytes)

        return file.absolutePath
    }
}

class BookDetailViewModelFactory(
    private val supabase: SupabaseClient,
    private val bookId: String,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookDetailViewModel(supabase, bookId, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
