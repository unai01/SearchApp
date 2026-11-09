package com.example.searchapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.searchapp.domain.model.Book
import com.example.searchapp.domain.usecase.SearchBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchBooksUseCase: SearchBooksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    private var searchJob: Job? = null
    private var searchVersion = 0L

    fun onQueryChange(query: String) {
        cancelPreviousSearch()

        _uiState.value = SearchUiState(query = query)
    }

    fun search() {
        val query = _uiState.value.query.trim()

        cancelPreviousSearch()

        if (query.isBlank()) {
            _uiState.value = SearchUiState(query = query)
            return
        }

        val currentVersion = searchVersion

        _uiState.value = SearchUiState(
            query = query,
            isLoading = true
        )

        searchJob = viewModelScope.launch {
            try {
                val books = searchBooksUseCase(query)

                if (currentVersion == searchVersion) {
                    _uiState.value = SearchUiState(
                        query = query,
                        books = books
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (currentVersion == searchVersion) {
                    _uiState.value = SearchUiState(
                        query = query,
                        error = "No se han podido cargar los libros."
                    )
                }
            }
        }
    }

    fun selectBook(book: Book) {
        _selectedBook.value = book
    }

    private fun cancelPreviousSearch() {
        searchVersion++
        searchJob?.cancel()
        searchJob = null
    }
}