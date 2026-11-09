package com.example.searchapp.domain.usecase

import com.example.searchapp.domain.model.Book
import com.example.searchapp.domain.repository.BookRepository
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(query: String): List<Book> {
        val normalizedQuery = query.trim()

        if (normalizedQuery.isBlank()) {
            return emptyList()
        }

        return repository.searchBooks(normalizedQuery)
    }
}