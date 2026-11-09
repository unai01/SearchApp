package com.example.searchapp.data.mapper

import com.example.searchapp.data.remote.BookDto
import com.example.searchapp.domain.model.Book

fun BookDto.toDomain(): Book {
    return Book(
        id = key.orEmpty(),
        title = title.orEmpty().ifBlank { "Sin título" },
        author = authors
            ?.joinToString(", ")
            ?.ifBlank { "Autor desconocido" }
            ?: "Autor desconocido",
        firstPublishYear = firstPublishYear,
        editionCount = editionCount ?: 0
    )
}