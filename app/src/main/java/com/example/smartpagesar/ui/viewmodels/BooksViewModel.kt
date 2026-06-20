package com.example.smartpagesar.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.UserBook
import io.github.jan.supabase.SupabaseClient
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

    init {
        loadDownloadedBooks()
    }

    fun loadDownloadedBooks() {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch

                // 1. Get all rows from user_books for this user
                val userBooks = supabase.postgrest["user_book"]
                    .select {
                        filter { eq("user_id", userId) }
                    }
                    .decodeList<UserBook>()

                if (userBooks.isEmpty()) {
                    _books.value = emptyList()
                    return@launch
                }

                // 2. Extract book IDs
                val bookIds = userBooks.map { it.book_id }

                // 3. Fetch the actual books
                val books = supabase.postgrest["Books"]
                    .select {
                        filter {
                            filter("id", FilterOperator.IN, "(${bookIds.joinToString(",")})")
                        }
                    }
                    .decodeList<Book>()

                _books.value = books

            } catch (e: Exception) {
                Log.e("BooksViewModel", "Error loading downloaded books", e)
            }
        }
    }

    fun deleteDownloadedBook(book: Book, context: Context) {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
                val shortId = book.short_id
                val folderName = "book$shortId"

                // 1️⃣ Elimina riga da user_book
                supabase.postgrest["user_book"]
                    .delete {
                        filter {
                            eq("user_id", userId)
                            eq("book_id", book.id)
                        }
                    }

                // 2️⃣ Elimina cartella locale: files/book{short_id}
                val bookFolder = File(context.filesDir, folderName)
                if (bookFolder.exists()) {
                    bookFolder.deleteRecursively()
                }

                // 3️⃣ Elimina immagini locali che iniziano con "book{short_id}-"
                val filesDir = File(context.filesDir, "images")
                filesDir.listFiles()?.forEach { file ->
                    val name = file.name
                    val parts = name.split("-")

                    if (parts.isNotEmpty() && parts[0] == folderName) {
                        file.delete()
                    }
                }

                // 4️⃣ Aggiorna lista libri scaricati
                loadDownloadedBooks()

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
