package com.example.smartpagesar.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.InteractiveModel
import com.example.smartpagesar.data.models.UserBook
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class DownloadBooksViewModel(
    private val supabase: SupabaseClient,
    private val context: Context
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books.asStateFlow()


    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            try {
                val result = supabase.postgrest["Books"]
                    .select() // loads all books
                    .decodeList<Book>()

                _books.value = result

            } catch (e: Exception) {
                Log.e("DownloadBooksVM", "Error loading books", e)
            }
        }
    }

    fun markBookAsDownloaded(bookId: String) {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch

                supabase.postgrest["user_book"].insert(
                    mapOf(
                        "user_id" to userId,
                        "book_id" to bookId
                    )
                )

                Log.d("DownloadBooksVM", "Book $bookId marked as downloaded")

            } catch (e: Exception) {
                Log.e("DownloadBooksVM", "Error marking book downloaded", e)
            }
        }
    }

    fun downloadBookWithModels(bookId: String, shortId: Int, onProgress: (Float)-> Unit) {
        viewModelScope.launch {

            // 1. Get all models for this book
            val models = supabase
                .from("interactive_model")
                .select {filter { eq("book_id", bookId) }}
                .decodeList<InteractiveModel>()
            val totalNumber = models.size
            var downloadedNumber = 0

            // 2. For each model, download files
            models.forEach { model ->

                // Download model file
                val modelBytes = supabase.storage
                    .from("ModelsImages")
                    .downloadPublic(model.model)

                val modelLocalPath = saveToInternalStorage(
                    fileName = "${model.id}.glb",
                    bytes = modelBytes,
                    folderName = "book${shortId}"
                )

                // Download image file
                val imageBytes = supabase.storage
                    .from("ModelsImages")
                    .downloadPublic(model.image)

                val imageLocalPath = saveToInternalStorage(
                    fileName = model.image,
                    bytes = imageBytes,
                    folderName = "images"
                )

                downloadedNumber++
                onProgress(downloadedNumber.toFloat()/totalNumber)
            }
            val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch
            // 4. Mark book as downloaded
            supabase.from("user_book").insert(
                UserBook(user_id = userId, book_id = bookId)
            )
        }
    }

    private fun saveToInternalStorage(
        folderName: String,
        fileName: String,
        bytes: ByteArray
    ): String {

        // Create the folder inside internal storage
        val dir = File(context.filesDir, folderName)
        if (!dir.exists()) dir.mkdirs()

        // Create the file inside that folder
        val file = File(dir, fileName)
        file.writeBytes(bytes)

        return file.absolutePath
    }


}

class DownloadBooksViewModelFactory(
    private val supabase: SupabaseClient,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadBooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DownloadBooksViewModel(supabase, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

