package com.example.searchapp.presentation.search

import androidx.lifecycle.SavedStateHandle
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
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val ERROR_NETWORK = "ERROR_NETWORK"
        const val ERROR_TIMEOUT = "ERROR_TIMEOUT"
        const val ERROR_SERVER = "ERROR_SERVER"
        const val ERROR_UNKNOWN = "ERROR_UNKNOWN"
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _selectedBook = MutableStateFlow(
        restoreSelectedBook()
    )
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
                        books = books,
                        hasSearched = true
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (currentVersion == searchVersion) {
                    _uiState.value = SearchUiState(
                        query = query,
                        error = classifyError(exception)
                    )
                }
            }
        }
    }

    fun selectBook(book: Book) {
        savedStateHandle["selectedBookId"] = book.id
        savedStateHandle["selectedBookTitle"] = book.title
        savedStateHandle["selectedBookAuthor"] = book.author
        savedStateHandle["selectedBookYear"] = book.firstPublishYear
        savedStateHandle["selectedBookEditions"] = book.editionCount

        _selectedBook.value = book
    }

    private fun restoreSelectedBook(): Book? {
        val id = savedStateHandle.get<String>("selectedBookId")
            ?: return null

        val title = savedStateHandle.get<String>("selectedBookTitle")
            ?: return null

        val author = savedStateHandle.get<String>("selectedBookAuthor")
            ?: return null

        return Book(
            id = id,
            title = title,
            author = author,
            firstPublishYear = savedStateHandle["selectedBookYear"],
            editionCount = savedStateHandle.get<Int>(
                "selectedBookEditions"
            ) ?: 0
        )
    }

    private fun classifyError(exception: Exception): String {
        return when (exception) {
            is SocketTimeoutException -> ERROR_TIMEOUT
            is UnknownHostException -> ERROR_NETWORK
            is HttpException -> ERROR_SERVER
            is IOException -> ERROR_NETWORK
            else -> ERROR_UNKNOWN
        }
    }

    private fun cancelPreviousSearch() {
        searchVersion++
        searchJob?.cancel()
        searchJob = null
    }
}