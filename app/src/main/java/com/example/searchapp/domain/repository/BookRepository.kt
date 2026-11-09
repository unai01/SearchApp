package com.example.searchapp.domain.repository

import com.example.searchapp.domain.model.Book

interface BookRepository {
    suspend fun searchBooks(query: String): List<Book>
}