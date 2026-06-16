package com.example.smartpagesar.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpagesar.data.models.Book
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadBooksViewModel(
    private val supabase: SupabaseClient
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
}

class DownloadBooksViewModelFactory(
    private val supabase: SupabaseClient
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadBooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DownloadBooksViewModel(supabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}