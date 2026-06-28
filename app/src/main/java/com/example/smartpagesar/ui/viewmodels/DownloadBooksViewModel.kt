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
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import kotlin.collections.emptyList

class DownloadBooksViewModel(
    private val supabase: SupabaseClient,
    private val context: Context,
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // 2. Combine the base list and query into a single public state for the UI
    val filteredBooks = combine(_books, _searchQuery) { booksList, query ->
        if (query.isBlank()) {
            booksList
        } else {
            // Adjust 'book.title' or 'book.name' depending on your Book model's properties
            booksList.filter { book ->
                book.title.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadBooks()
    }

    fun loadBooks() {

        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch
                val userBooks = supabase.postgrest["user_book"]
                    .select {
                        filter { eq("user_id", userId) }
                    }
                    .decodeList<UserBook>()

                val ids = userBooks.map { it.book_id }
                val formattedIds = "(${ids.joinToString(",") { it }})"

                val result = supabase.postgrest["Books"]
                    .select{
                        filter {
                            filterNot("id", FilterOperator.IN, formattedIds)
                        }
                    }
                    .decodeList<Book>()


                _books.value = result

            } catch (e: Exception) {
                Log.e("DownloadBooksVM", "Error loading books", e)
            }
        }
    }

    fun downloadBookWithModels(bookId: String, shortId: Int, onProgress: (Float)-> Unit, onIsDownloading: (Boolean)->Unit) {
        viewModelScope.launch {
            onIsDownloading(true)
            // 1. Get all models for this book
            val models = supabase
                .from("interactive_model")
                .select {filter { eq("book_id", bookId) }}
                .decodeList<InteractiveModel>()
            val totalNumber = models.size
            var downloadedNumber = 0

            models.forEach { model ->

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

                downloadedNumber++
                onProgress(downloadedNumber.toFloat()/totalNumber)
            }
            onIsDownloading(false)
            val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch
            // 4. Mark book as downloaded
            supabase.from("user_book").insert(
                UserBook(user_id = userId, book_id = bookId)
            )
        }

    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
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

