package com.example.smartpagesar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.InteractiveModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val supabase: SupabaseClient,
    val bookId: String
) : ViewModel() {

    private val _models = MutableStateFlow<List<InteractiveModel>>(emptyList())
    private val _book = MutableStateFlow(Book("",0,"","","",0,0))
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
}

class BookDetailViewModelFactory(
    private val supabase: SupabaseClient,
    private val bookId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookDetailViewModel(supabase, bookId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
