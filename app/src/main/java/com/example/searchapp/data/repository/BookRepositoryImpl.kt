package com.example.searchapp.data.repository

import com.example.searchapp.data.cache.BookSearchCache
import com.example.searchapp.data.mapper.toDomain
import com.example.searchapp.data.remote.OpenLibraryApi
import com.example.searchapp.domain.model.Book
import com.example.searchapp.domain.repository.BookRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val api: OpenLibraryApi,
    private val cache: BookSearchCache
) : BookRepository {

    override suspend fun searchBooks(query: String): List<Book> {
        cache.get(query)?.let { return it }

        val books = api.searchBooks(query)
            .books
            .map { it.toDomain() }

        currentCoroutineContext().ensureActive()

        cache.put(query, books)

        return books
    }
}