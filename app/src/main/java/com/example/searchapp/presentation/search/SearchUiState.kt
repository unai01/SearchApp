package com.example.searchapp.presentation.search

import com.example.searchapp.domain.model.Book

data class SearchUiState(
    val query: String = "",
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)