package com.example.smartpagesar.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.UserBook
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class BooksViewModel(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    init {
        viewModelScope.launch {
            _isLoading.value = true

            supabase.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val userId = status.session.user?.id
                        if (userId != null) {
                            fetchBooksForUser(userId)
                        } else {
                            _books.value = emptyList()
                            _isLoading.value = false
                        }
                    }

                    is SessionStatus.NotAuthenticated -> {
                        _books.value = emptyList()
                        _isLoading.value = false
                    }
                    else -> { /* Keep loading */ }
                }
            }
        }
    }

    // Changed to a standard suspending function (no internal viewModelScope.launch)
    private suspend fun fetchBooksForUser(userId: String) {
        try {
            // 1. Get all rows from user_books for this user
            val userBooks = supabase.postgrest["user_book"]
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<UserBook>()

            if (userBooks.isEmpty()) {
                _books.value = emptyList()
                return
            }

            // 2. Map IDs and fetch the actual books
            val bookIds = userBooks.map { it.book_id }
            val fetchedBooks = supabase.postgrest["Books"]
                .select {
                    filter {
                        filter("id", FilterOperator.IN, "(${bookIds.joinToString(",")})")
                    }
                }
                .decodeList<Book>()

            _books.value = fetchedBooks

        } catch (e: Exception) {
            Log.e("BooksViewModel", "Error loading downloaded books", e)
        } finally {
            // This ensures loading turns off whether the try succeeds or catches an error
            _isLoading.value = false
        }
    }

    fun deleteDownloadedBook(book: Book, context: Context) {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
                val shortId = book.short_id
                val folderName = "book$shortId"

                supabase.postgrest["user_book"]
                    .delete {
                        filter {
                            eq("user_id", userId)
                            eq("book_id", book.id)
                        }
                    }

                val bookFolder = File(context.filesDir, folderName)
                if (bookFolder.exists()) {
                    bookFolder.deleteRecursively()
                }

                val filesDir = File(context.filesDir, "images")
                filesDir.listFiles()?.forEach { file ->
                    val name = file.name
                    val parts = name.split("-")

                    if (parts.isNotEmpty() && parts[0] == folderName) {
                        file.delete()
                    }
                }
                fetchBooksForUser(userId)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

class BooksViewModelFactory(
    private val supabase: SupabaseClient
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BooksViewModel(supabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
