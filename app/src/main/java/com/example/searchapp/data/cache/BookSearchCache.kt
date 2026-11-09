package com.example.searchapp.data.cache

import com.example.searchapp.domain.model.Book
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookSearchCache @Inject constructor() {

    private data class CacheEntry(
        val books: List<Book>,
        val createdAt: Long
    )

    private val entries = mutableMapOf<String, CacheEntry>()

    private val ttlMillis = 3 * 60 * 1000L

    internal var currentTimeMillis: () -> Long = {
        System.nanoTime() / 1_000_000L
    }

    @Synchronized
    fun get(query: String): List<Book>? {
        val key = normalize(query)
        val entry = entries[key] ?: return null

        val elapsed = currentTimeMillis() - entry.createdAt

        if (elapsed >= ttlMillis || elapsed < 0) {
            entries.remove(key)
            return null
        }

        return entry.books
    }

    @Synchronized
    fun put(query: String, books: List<Book>) {
        entries[normalize(query)] = CacheEntry(
            books = books.toList(),
            createdAt = currentTimeMillis()
        )
    }

    private fun normalize(query: String): String =
        query.trim().lowercase()
}